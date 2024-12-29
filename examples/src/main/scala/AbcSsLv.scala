/*
AbcSsLv.scala

Inference for the parameters of a Lotka-Volterra model using ABC with summary statistics

 */

object AbcSsLvO:

  import smfsb.*
  import scala.io.Source
  import breeze.linalg.*
  import breeze.numerics.*
  import breeze.stats.*
  import breeze.stats.distributions.*
  import breeze.stats.distributions.Rand.VariableSeed.randBasis

  def statePriorSample() =
    DenseVector(Poisson(50.0).draw(), Poisson(100.0).draw())

  def rprior = exp(
    DenseVector(
      Uniform(-3.0, 3.0).draw(),
      Uniform(-8.0, -2.0).draw(),
      Uniform(-4.0, 2.0).draw()
    )
  )

  def distance(real: DoubleState)(sim: DoubleState): Double =
    math.sqrt(sum((real - sim).map(x => x * x)))

  def ssds(ts: Ts[DoubleState]): DoubleState =
    val v = DenseVector(ts.map(_._2(0)).toArray)
    import breeze.stats.*
    val m = meanAndVariance(v)
    val acf = Mcmc.acf(v.data.toVector, 3)
    DenseVector(m.mean, math.sqrt(m.variance), acf(1), acf(2), acf(3))

  def ss(ts: Ts[IntState]): DoubleState =
    ssds(ts.map { case (t, v) => (t, v.map(_.toDouble)) })

  def step(p: DoubleState) =
    Step.gillespie(SpnModels.lv[IntState](p), maxH = 1e5)

  @main def abcSsLv() =
    println("ABC rejection demo (with summary stats)...")
    val n = 100000 // required number of iterations from the ABC algorithm
    val fraction = 0.01 // fraction of accepted ABC samples
    val rawData = Source.fromFile("LVpreyNoise10.txt").getLines()
    val data =
      ((0 to 30 by 2).toList zip rawData.toList).map((x: (Int, String)) =>
        (x._1.toDouble, DenseVector(x._2.toDouble))
      )
    def ss1(p: DoubleState): DoubleState = ss(
      Sim.ts[IntState](statePriorSample(), 0.0, 30.0, 2.0, step(p))
    )
    println("Starting ABC pilot run...")
    val out1 = Abc.run(1500, rprior, ss1 _)
    println("Pilot run completed.")
    val outMat = Mcmc.toDMD(out1.map(_._2).seq)
    val sds = sqrt(variance(outMat(::, *))).t
    println(sds)
    def ss2(ts: Ts[IntState]): DoubleState = ss(ts) /:/ sds
    val ssd = ssds(data) /:/ sds
    val dist = distance(ssd) _
    def rdist(p: DoubleState): Double = dist(
      ss2(Sim.ts[IntState](statePriorSample(), 0.0, 30.0, 2.0, step(p)))
    )
    println("Starting main ABC run...")
    val out = Abc.run(n, rprior, rdist _)
    println("Main ABC run completed.")
    // Abc.summary(out)
    val distances = (out map (_._2)).seq
    import breeze.stats.DescriptiveStats.*
    val cutoff = percentile(distances, fraction)
    val accepted = out filter (_._2 < cutoff)
    val laccepted = (accepted map (x => log(x._1)))
    Mcmc.summary(laccepted.seq, true)
    println("Done.")

// eof
