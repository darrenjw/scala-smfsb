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


  /*
  def pfMll[S: State, P: Parameter, O: Observation](
    n: Int,
    simx0: (P) => (Int, Time) => Vector[S],
    t0: Time,
    stepFun: (P) => (S, Time, Time) => S,
    dataLik: (P) => (S, O) => LogLik,
    data: Ts[O]
  ): (P => LogLik) = {
    val (times, obs) = data.unzip
    val deltas = diff(t0 :: times)
    (th: P) => {
      val x0 = simx0(th)(n, t0)
      @tailrec def pf(ll: LogLik, x: Vector[S], t: Time, deltas: Iterable[Time], obs: List[O]): LogLik =
        obs match {
          case Nil => ll
          case head :: tail => {
            val xp = if (deltas.head == 0) x else (x map { stepFun(th)(_, t, deltas.head) })
            val lw = xp map { dataLik(th)(_, head) }
            val max = lw.max
            val w = lw map { x => exp(x - max) }
            val rows = sample(n, DenseVector(w.toArray))
            val xpp = rows map { xp(_) }
            pf(ll + max + log(mean(w)), xpp, t + deltas.head, deltas.tail, tail)
          }
        }
      pf(0, x0, t0, deltas, obs)
    }
  }

   */

}

// eof

