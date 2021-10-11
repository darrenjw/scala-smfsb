/*
AbcLv.scala

Inference for the parameters of a Lotka-Volterra model using ABC

 */

object AbcLvO:

  import smfsb.*
  import scala.io.Source
  import breeze.linalg.*
  import breeze.numerics.*
  import breeze.stats.distributions.*
  import breeze.stats.distributions.Rand.VariableSeed.randBasis

  // state prior
  def statePriorSample = DenseVector(Poisson(50.0).draw(), Poisson(100.0).draw())
  def rprior = exp(DenseVector(Uniform(-3.0,3.0).draw(),
    Uniform(-8.0,-2.0).draw(),Uniform(-4.0,2.0).draw()))
  def distance(real: Ts[DoubleState])(sim: Ts[IntState]): Double =
    math.sqrt((real zip sim).map{case (r,s) => r._2(0)-s._2(0)}.map(x=>x*x).reduce(_+_))
  def step(p: DoubleState) = Step.gillespie(SpnModels.lv[IntState](p), maxH=1e5)

  @main def abcLv() =
    println("ABC rejection demo...")
    val n = 100000 // required number of iterations from the ABC algorithm
    val fraction = 0.01 // fraction of accepted ABC samples
    val rawData = Source.fromFile("LVpreyNoise10.txt").getLines()
    val data = ((0 to 30 by 2).toList zip rawData.toList).
      map((x: (Int,String)) => (x._1.toDouble, DenseVector(x._2.toDouble)))
    val dist = distance(data) _
    def rdist(p: DoubleState): Double = dist(
      Sim.ts[IntState](statePriorSample,0.0,30.0,2.0,step(p)))
    println("Starting ABC run now...")
    val out = Abc.run(n,rprior,rdist _)
    println("ABC run completed.")
    val distances = (out map (_._2)).seq
    import breeze.stats.DescriptiveStats.*
    val cutoff = percentile(distances, fraction)
    val accepted = out filter (_._2 < cutoff)
    val laccepted = accepted map (x => log(x._1))
    Mcmc.summary(laccepted.seq,true)
    println("Done.")
 

// eof

