/*
AbcSmcLv.scala

Inference for the parameters of a Lotka-Volterra model using ABC-SMC with summary statistics

 */

import smfsb._
import scala.io.Source
import breeze.linalg._
import breeze.numerics._
import breeze.stats._
import breeze.stats.distributions._

import breeze.stats.distributions.Rand.VariableSeed.randBasis

object AbcSmcLv {

  def statePriorSample = DenseVector(Poisson(50.0).draw(), Poisson(100.0).draw())

  def rprior = DenseVector(Uniform(-3.0,3.0).draw(),Uniform(-8.0,-2.0).draw(),Uniform(-4.0,2.0).draw())

  def dprior(p: DoubleState): LogLik = Uniform(-3.0,3.0).logPdf(p(0)) +
    Uniform(-8.0,-2.0).logPdf(p(1)) + Uniform(-4.0,2.0).logPdf(p(2))

  def rperturb(p: DoubleState): DoubleState = p +:+
    DenseVector(Gaussian(0.0,0.5).sample(3).toArray)

  def dperturb(pNew: DoubleState, pOld: DoubleState): Double =
    sum((pNew -:- pOld).map(Gaussian(0.0,0.5).logPdf(_)))

  def distance(real: DoubleState)(sim: DoubleState): Double =
    math.sqrt(sum((real-sim).map(x => x*x)))

  def ssds(ts: Ts[DoubleState]): DoubleState = {
    val v = DenseVector(ts.map(_._2(0)).toArray)
    import breeze.stats._
    val m = meanAndVariance(v)
    val acf = Mcmc.acf(v.data.toVector, 3)
    DenseVector(m.mean, math.sqrt(m.variance), acf(1), acf(2), acf(3))
  }

  def ss(ts: Ts[IntState]): DoubleState = {
    ssds(ts.map{case (t,v) => (t, v.map(_.toDouble))})
  }

  def step(p: DoubleState) = Step.gillespie(SpnModels.lv[IntState](exp(p)), maxH=1e5)

  def main(args: Array[String]): Unit = {
    println("ABC-SMC demo (with summary stats)...")
    val N = 5000 // required number of iterations from the ABC-SMC algorithm
    val rawData = Source.fromFile("LVpreyNoise10.txt").getLines()
    val data = ((0 to 30 by 2).toList zip rawData.toList).map((x: (Int,String)) => (x._1.toDouble, DenseVector(x._2.toDouble)))
    def ss1(p: DoubleState): DoubleState = ss(Sim.ts[IntState](statePriorSample,0.0,30.0,2.0,step(p)))
    println("Starting ABC pilot run...")
    val out1 = Abc.run(1500,rprior,ss1 _)
    println("Pilot run completed.")
    val outMat = Mcmc.toDMD(out1.map(_._2).seq)
    val sds = sqrt(variance(outMat(::, *))).t
    println(sds)
    def ss2(ts: Ts[IntState]): DoubleState = ss(ts) /:/ sds
    val ssd = ssds(data) /:/ sds
    val dist = distance(ssd) _
    def rdist(p: DoubleState): Double = dist(ss2(Sim.ts[IntState](statePriorSample,0.0,30.0,2.0,step(p))))
    println("Starting main ABC-SMC run...")
    val out = Abc.smc(N,rprior,dprior,rdist,rperturb,dperturb,5,8,true)
    println("Main ABC-SMC run completed.")
    Mcmc.summary(out.seq,true)
    val eout = out map (exp(_))
    Mcmc.summary(eout.seq,true)
    println("Done.")
  }

}


// eof

