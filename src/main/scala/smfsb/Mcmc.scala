/*

Mcmc.scala

Functions relating to the construction and analysis of MCMC algorithms, especially MH algorithms for PMMH

 */

package smfsb

import Types._

object Mcmc {

  import breeze.stats.distributions.Uniform

  def nextValue[P](
    logLik: P => LogLik, rprop: P => P, dprop: (P, P) => LogLik, dprior: P => LogLik
  )(
    current: (P, LogLik)
  ): (P, LogLik) = {
    val (p,ll) = current
    val prop = rprop(p)
    val llprop = logLik(prop)
    val a = llprop - ll + dprior(prop) - dprior(p) + dprop(p, prop) - dprop(prop, p)
    if (math.log(Uniform(0.0,1.0).draw) < a)
      (prop, llprop) else (p, ll)
  }

  def mhStream[P](
    init: P, logLik: P => LogLik, rprop: P => P,dprop: (P, P) => LogLik, dprior: P => LogLik
  ): Stream[P] = {
    val kernel = nextValue(logLik, rprop, dprop, dprior) _
    Stream.iterate((init, Double.MinValue)){
    case (p,ll) => kernel(p, ll)
    }.map(_._1)
  }

  import breeze.linalg._
  import breeze.numerics._
  def toDMD[P: CsvRow](s: Stream[P]): DenseMatrix[Double] = {
    val n = s.length
    val p = s(0).toDvd.length
    val m = new DenseMatrix[Double](n, p)
    s.zipWithIndex.foreach{case (v, i) => m(i, ::) := v.toDvd.t} 
    m
  }


}

// eof

