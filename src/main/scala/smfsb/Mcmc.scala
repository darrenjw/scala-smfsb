/*

Mcmc.scala

Functions relating to the construction and analysis of MCMC algorithms, especially MH algorithms for PMMH

 */

package smfsb

/** Functions for constucting generic Metropolis-Hastings MCMC algorithms, and
  * associated utilities. Can be used in conjunction with an unbiased estimate
  * of marginal model likelihood for constructing pseudo-marginal MCMC
  * algorithms, such as PMMH pMCMC.
  */
object Mcmc {

  import breeze.stats.distributions.Uniform

  import breeze.stats.distributions.Rand.VariableSeed.randBasis

  /** Function for executing one step of a MH algorithm. Called by `mhStream`.
    */
  def nextValue[P](
      logLik: P => LogLik,
      rprop: P => P,
      dprop: (P, P) => LogLik,
      dprior: P => LogLik,
      verb: Boolean = false
  )(
      current: (P, LogLik)
  ): (P, LogLik) = {
    if (verb)
      println(current)
    val (p, ll) = current
    val prop = rprop(p)
    val llprop = logLik(prop)
    val a =
      llprop - ll + dprior(prop) - dprior(p) + dprop(p, prop) - dprop(prop, p)
    if (math.log(Uniform(0.0, 1.0).draw()) < a)
      (prop, llprop)
    else (p, ll)
  }

  /** Function to construct a generic Metropolis-Hastings MCMC algorithm for
    * Bayesian inference. Note that this algorithm avoids re-computation of the
    * log-likelihood associated with the current state, and is therefore
    * suitable for use with the log of an unbiased estimate of likelihood for
    * the constuction of pseudo-marginal "exact approximate" MCMC algorithms.
    *
    * @param init
    *   The initial state of the MCMC algorithm
    * @param logLik
    *   The log-likelihood of the model
    * @param rprop
    *   A function to sample from a proposal distribution
    * @param dprop
    *   A function to evaluate the log-likelihood of the proposal transition
    *   kernel
    * @param dprior
    *   A function to evaluate the log of the prior density
    * @param verb
    *   Should the function print diagnostic information to the console at each
    *   iteration?
    *
    * @return
    *   An infinite `Stream` corresponding to the MCMC chain. Note that this can
    *   be processed with typical Scala combinators such as `drop` (for burn-in)
    *   and `take` (for run-length). If `Types` are imported, there is also a
    *   `thin` method (which can be used for thinning the chain).
    */
  def mhStream[P](
      init: P,
      logLik: P => LogLik,
      rprop: P => P,
      dprop: (P, P) => LogLik,
      dprior: P => LogLik,
      verb: Boolean = false
  ): LazyList[P] = {
    val kernel = nextValue(logLik, rprop, dprop, dprior, verb) _
    LazyList
      .iterate((init, Double.MinValue)) { case (p, ll) =>
        kernel(p, ll)
      }
      .map(_._1)
  }

  import breeze.linalg._
  import breeze.numerics._
  import scala.collection.GenSeq

  /** Utility function to convert a *finite* `Stream` (or other collection) to a
    * Breeze `DenseMatrix[Double]`.
    *
    * @param s
    *   Input stream/collection, which must be *finite*.
    *
    * @return
    *   A matrix with rows corresponding to iterations and columns corresponding
    *   to variables.
    */
  def toDMD[P: CsvRow](s: Seq[P]): DenseMatrix[Double] = {
    val n = s.length
    val p = s(0).toDvd.length
    val m = new DenseMatrix[Double](n, p)
    s.zipWithIndex.foreach { case (v, i) => m(i, ::) := v.toDvd.t }
    m
  }

  /** Autocorrelation function
    *
    * @param x
    *   Input data
    * @param lm
    *   Maximum lag required
    *
    * @return
    *   A vector of autocorrelations
    */
  def acf(x: Seq[Double], lm: Int): DenseVector[Double] = {
    val m = x.sum / x.length
    val xx = x map (_ - m)
    val v = (xx.map(xi => xi * xi)).reduce(_ + _) / x.length
    val a = ((0 to lm) map ((k: Int) =>
      (((xx
        .zip(xx drop k))
        .map(p => p._1 * p._2))
        .reduce(_ + _)) / (v * xx.length)
    )).toArray
    DenseVector(a)
  }

  // TODO: Print summary stats nicely to console
  /** Generate some basic diagnostics associated with an MCMC run. Called purely
    * for the side-effect of generating output on the console.
    *
    * @param m
    *   A matrix, such as generated by `toDMD` containing MCMC output
    * @param plt
    *   Generate plots?
    * @param lm
    *   Max lag for ACF plot
    */
  def summary(
      m: DenseMatrix[Double],
      plt: Boolean = true,
      lm: Int = 50
  ): Unit = {
    import breeze.plot._
    import breeze.stats.{hist => stHist, _}
    val n = m.rows
    val v = m.cols
    println("" + n + " iterates and " + v + " variables")
    val me = mean(m(::, *))
    println("Sample means: " + me.t.toCsv)
    val vari = variance(m(::, *))
    println("Sample variances: " + vari.t.toCsv)
    println("Sample SDs: " + sqrt(vari).t.toCsv)
    val med = median(m(::, *))
    println("Sample medians: " + med.t.toCsv)
    if (plt) {
      val f = Figure("MCMC Diagnostic plots")
      (0 until v).foreach(i => {
        val p0 = f.subplot(v, 3, 3 * i)
        p0 += plot(DenseVector((1 to n).map(_.toDouble).toArray), m(::, i))
        p0.xlabel = "Iteration"
        p0.ylabel = "Value"
        p0.title = "Trace plot"
        val p1 = f.subplot(v, 3, 3 * i + 1)
        p1 += plot(
          DenseVector((0 to lm).map(_.toDouble).toArray),
          acf(m(::, i).toArray.toVector, lm)
        )
        p1.xlabel = "Lag"
        p1.ylabel = "ACF"
        p1.title = "ACF"
        val p2 = f.subplot(v, 3, 3 * i + 2)
        p2 += hist(m(::, i), 50)
        p2.xlabel = "Value"
        p2.title = "Marginal density"
      })
      f.saveas("McmcPlot.png")
    }
  }

  /** Wrapper around the other `summary` function which takes a `Stream`
    *
    * @param s
    *   A *finite* stream of MCMC iterations.
    * @param plot
    *   Generate plots?
    */
  def summary[P: CsvRow](s: Seq[P], plot: Boolean): Unit = {
    summary(toDMD(s), plot)
  }

}

// eof
