/*
Mll.scala

Particle filter for marginal log-likelihood estimation

 */

package smfsb

import annotation.tailrec

/** Functions associated with particle filtering of Markov process models
  * against time series data and the computation of marginal model likelihoods.
  */
object Mll {

  import breeze.linalg.DenseVector
  import breeze.stats.distributions.Multinomial
  import spire.math._
  import spire.implicits._

  import breeze.stats.distributions.Rand.VariableSeed.randBasis

  // TODO: support alternative resampling methods, such as systematic

  // TODO: scaladoc
  def sample(n: Int, prob: DenseVector[Double]): Vector[Int] = {
    Multinomial(prob).sample(n).toVector
  }

  // TODO: Make collection-independent, supporting parallel execution - probably need to tweak the multinomial resampling
  private def bpfUpdate[S, O](
      dataLik: (S, O) => LogLik,
      stepFun: (S, Time, Time) => S
  )(
      x: Vector[S],
      t0: Time,
      t1: Time,
      o: O
  ): (LogLik, Vector[S]) = {
    val xp = x map (stepFun(_, t0, t1 - t0))
    val lw = xp map (dataLik(_, o))
    val max = lw reduce (math.max(_, _))
    val rw = lw map (lwi => math.exp(lwi - max))
    val srw = rw.sum
    val l = rw.length
    val rows = sample(l, DenseVector(rw.toArray))
    val rx = rows map { xp(_) } // TODO: replace with flatMap
    (max + math.log(srw / l), rx)
  }

  /** A simple bootstrap particle filter with multinomial resampling. Note that
    * it supports time series of observations on irregular time grids. Note that
    * this is the function which does most of the work for `pfMll`.
    *
    * @param x0
    *   A sample from the prior on the state at time `t0`. The size of the
    *   sample determines the number of particles used in the particle filter.
    * @param t0
    *   The time corresponding to the prior sample
    * @param data
    *   The observed data in the form of a time series
    * @param dataLik
    *   The log-likelihood of the observation model
    * @param stepFun
    *   The transition kernel of the underlying process
    *
    * @return
    *   A triple: the first element is the log of an unbiased estimate of the
    *   marginal likelihood of the model, the second is just the final time
    *   point, and the third is a sample from the posterior distribution of the
    *   final time point.
    */
  def bpf[S: State, O](
      x0: Vector[S],
      t0: Time,
      data: Ts[O],
      dataLik: (S, O) => LogLik,
      stepFun: (S, Time, Time) => S
  ): (LogLik, Time, Vector[S]) = {
    val updater = bpfUpdate[S, O](dataLik, stepFun) _
    data.foldLeft((0.0, t0, x0))((prev, obs) => {
      val (t, o) = obs
      val (oll, ot, xx) = prev
      val (ll, nx) = updater(xx, ot, t, o)
      (oll + ll, t, nx)
    })
  }

  /** A function for evaluating marginal log-likelihoods of parametrised models.
    * It basically just threads a parameter value through the arguments required
    * for `bpf`.
    *
    * @param simX0
    *   A function which when provided with a parameter value will return a
    *   sample from the prior on the intial state of a Markov process model. The
    *   size of the sample will determine the number of particles used in the
    *   particle filter.
    * @param t0
    *   The intial time corresponding to the prior sample
    * @param stepFun
    *   A function, which when provided with a parameter value, will return the
    *   transition kernel of a Markov process.
    * @param dataLik
    *   A function, which when provided with a parameter value, will return the
    *   log-likelihood of the observation model
    * @param data
    *   The observed data in the form of a time series
    */
  def pfMll[P, S: State, O](
      simX0: P => Vector[S],
      t0: Time,
      stepFun: P => (S, Time, Time) => S,
      dataLik: P => (S, O) => LogLik,
      data: Ts[O]
  ): (P => LogLik) = (th: P) =>
    bpf(simX0(th), t0, data, dataLik(th), stepFun(th))._1

}

// eof
