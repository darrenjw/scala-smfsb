/*
Step.scala

Step functions for forward simulation

 */

package smfsb

import annotation.tailrec
import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import Types._

object Step {

  def gillespie(n: Spn[IntState]): (IntState, Time, Time) => IntState = {
    val Sto = (n.post - n.pre).t
    (x: IntState, t0, dt) => {
      @tailrec
      def go(x: IntState, t0: Time, dt: Time): IntState = {
        if (dt <= 0.0) x else {
          val h = n.h(x, t0)
          val h0 = sum(h)
          val t = if (h0 < 1e-50) 1e99 else new Exponential(h0).draw
          if (t > dt) x else {
            val i = Multinomial(h).sample
            go(x + Sto(::, i), t0 + t, dt - t)
          }
        }
      }
      go(x, t0, dt)
    }
  }




}



// eof
