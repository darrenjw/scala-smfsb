/*
Mll.scala

Particle filter for marginal log-likelihood estimation

 */

package smfsb

import annotation.tailrec

object Mll {

  import breeze.linalg.DenseVector
  import breeze.stats.distributions.Multinomial
  import spire.math._
  import spire.implicits._
  import Types._

  def sample(n: Int, prob: DenseVector[Double]): Vector[Int] = {
    Multinomial(prob).sample(n).toVector
  }


  // TODO: Make collection independent, supporting parallel execution - probably need to tweak the multinomial resampling
  def bpfUpdate[S, O](
    dataLik: (S,O) => LogLik, stepFun: (S, Time, Time) => S
  )(
    x: Vector[S], t0: Time, t1: Time, o: O
  ): (LogLik, Vector[S]) = {
    val xp = x map (stepFun(_, t0, t1-t0))
    val lw = xp map (dataLik(_, o))
    val max = lw reduce (math.max(_,_))
    val rw = lw map (lwi => math.exp(lwi-max))
    val srw = rw.sum
    val l = rw.length
    val rows = sample(l, DenseVector(rw.toArray))
    val rx = rows map { xp(_) }
    (max + math.log(srw/l), rx)
  }

  def bpf[S: State, O: Observation](
    x0: Vector[S], t0: Time, data: Ts[O], dataLik: (S, O) => LogLik, stepFun: (S, Time, Time) => S
  ): (LogLik, Time, Vector[S]) = {
    val updater = bpfUpdate[S, O](dataLik, stepFun) _
    data.foldLeft((0.0, t0, x0))((prev, obs) => {
      val (t, o) = obs
      val (oll, ot, xx) = prev
      val (ll, nx) = updater(xx , ot , t , o)
      (oll+ll, t, nx)
    })
  }

def pfMll[P, S: State, O: Observation](
  simX0: P => Vector[S],
  t0: Time,
  stepFun: P => (S, Time, Time) => S,
  dataLik: P => (S, O) => LogLik,
  data: Ts[O]
): (P => LogLik) = (th: P) => 
  bpf(simX0(th), t0, data, dataLik(th), stepFun(th))._1






}

// eof

