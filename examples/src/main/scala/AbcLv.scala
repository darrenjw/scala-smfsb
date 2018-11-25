/*
AbcLv.scala

Inference for the parameters of a Lotka-Volterra model using ABC

 */

import smfsb._
import Types._
import scala.io.Source
import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._

object AbcLv {

  // state prior
  def statePriorSample = DenseVector(Poisson(50.0).draw, Poisson(100.0).draw)
  // def statePrior(n: Int) = (1 to n).map(i => statePriorSample).toVector
  def rprior = exp(DenseVector(Uniform(-3.0,3.0).draw,Uniform(-8.0,-2.0).draw,Uniform(-4.0,2.0).draw))
  // MCMC proposal
  def rprop(p: DoubleState, tune: Double = 0.01): DoubleState = p*exp(DenseVector(Gaussian(0.0,0.01).sample(3).toArray)) 
  def dprop(n: DoubleState, o: DoubleState): Double = 1.0 // Not really true but OK here...
  // data log-likelihood
  // def obsLik(th: DoubleState)(s: IntState, o: DoubleState): LogLik =
  //  Gaussian(s.data(0).toDouble, 10.0).logPdf(o(0))
  def distance(real: Ts[DoubleState])(sim: Ts[IntState]): Double =
    math.sqrt((real zip sim).map{case (r,s) => r._2(0)-s._2(0)}.map(x=>x*x).reduce(_+_))
  def step(p: DoubleState) = Step.gillespie(SpnModels.lv[IntState](p))

  def main(args: Array[String]): Unit = {
    println("ABC rejection demo...")
    //val N = 100 // number of particles in the particle filter
    val n = 200 // required number of iterations from the ABC algorithm
    //val thin = 5 // thinning
    //val burn = 10 // initial discarded MCMC iterations
    //val tune = 0.01 // tuning parameter of the MH proposal
    // first read in the observational data
    val rawData = Source.fromFile("LVpreyNoise10.txt").getLines
    val data = ((0 to 30 by 2).toList zip rawData.toList).map((x: (Int,String)) => (x._1.toDouble, DenseVector(x._2.toDouble)))
    val dist = distance(data) _
    def rdist(p: DoubleState): Double = dist(Sim.ts[IntState](statePriorSample,0.0,30.0,2.0,step(p)))
    //println(data)
    //Sim.plotTs(data)
    println("Starting ABC run now...")
    val out = Abc.run(n,rprior,rdist _)
    println("ABC run completed.")
    val distances = (out map (_._2)).seq
    import breeze.stats.DescriptiveStats._
    val cutoff = percentile(distances, 0.1)
    val accepted = out filter (_._2 < cutoff)
    println(accepted.length)
    println("Done.")
  }

}


// eof

