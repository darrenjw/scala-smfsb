/*
PMCMC.scala

Inference for the parameters of a Lotka-Volterra model using PMMH particle MCMC

 */

import smfsb.*
import scala.io.Source
import breeze.linalg.*
import breeze.numerics.*
import breeze.stats.distributions.*
import breeze.stats.distributions.Rand.VariableSeed.randBasis

// state prior
def statePriorSample = DenseVector(Poisson(50.0).draw(), Poisson(100.0).draw())
def statePrior(n: Int) = (1 to n).map(i => statePriorSample).toVector
// MCMC proposal
def rprop(p: DoubleState, tune: Double = 0.01): DoubleState =
  p * exp(DenseVector(Gaussian(0.0, 0.01).sample(3).toArray))
def dprop(n: DoubleState, o: DoubleState): Double =
  1.0 // Not really true but OK here...
// data log-likelihood
def obsLik(th: DoubleState)(s: IntState, o: DoubleState): LogLik =
  Gaussian(s.data(0).toDouble, 10.0).logPdf(o(0))

@main def pMCMC() =
  println("PMCMC demo...")
  val N = 100 // number of particles in the particle filter
  val n = 5000 // required number of iterations from the PMMH algorithm
  val thin = 5 // thinning
  val burn = 10 // initial discarded MCMC iterations
  val tune = 0.01 // tuning parameter of the MH proposal
  // first read in the observational data
  val rawData = Source.fromFile("LVpreyNoise10.txt").getLines()
  val data =
    ((0 to 30 by 2).toList zip rawData.toList).map((x: (Int, String)) =>
      (x._1.toDouble, DenseVector(x._2.toDouble))
    )
  // Sim.plotTs(data)
  // now create the inferential model
  val mll = Mll.pfMll[DenseVector[Double], IntState, DoubleState](
    (p: DenseVector[Double]) => statePrior(N),
    0.0,
    (p: DenseVector[Double]) => Step.gillespie(SpnModels.lv[IntState](p)),
    obsLik,
    data
  )
  // now create an MCMC stream
  val s = Mcmc.mhStream(
    DenseVector(1.0, 0.005, 0.6),
    mll,
    rprop(_: DoubleState, tune),
    dprop,
    (p: DoubleState) => 1.0,
    verb = false
  )
  val out = s.drop(burn).thin(thin).take(n)
  println("Starting PMMH run now...")
  out.zipWithIndex.foreach(println)
  Mcmc.summary(out, true)
  println("Done.")

// eof
