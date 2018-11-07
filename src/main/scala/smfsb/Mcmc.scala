/*

Mcmc.scala

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
    val a = llprop - ll + dprior(prop) - dprior(p) + dprop(p,prop) - dprop(prop,p)
    if (math.log(Uniform(0.0,1.0).draw) < a)
      (prop, llprop) else (p, ll)
  }



}

// eof

