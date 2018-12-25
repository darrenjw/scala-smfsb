/*
Spatial.scala

All functions and utilities relating to spatial simulation

 */

package smfsb

import collection.GenSeq
import annotation.tailrec
import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._

/**
  * All functions and utilities relating to spatial simulation
  */
object Spatial {

  // TODO: Selective hazard recalculation, etc. Currently _very_ inefficient...
  def gillespie1d(
    n: Spn[IntState],
    d: DoubleState,
    minH: Double = 1e-20,
    maxH: Double = 1e6
  ): (GenSeq[IntState], Time, Time) => GenSeq[IntState] = {
    val Sto = (n.post - n.pre).t
    val v = Sto.rows // number of species
    assert(d.length == v)
      (x0: GenSeq[IntState], t0, dt) => {
      val nv = x0.length
      @tailrec
      def go(x: GenSeq[IntState], t0: Time, dt: Time): GenSeq[IntState] = {
        if (dt <= 0.0) x else {
          val hr = x map (n.h(_,t0))
          val hrs = hr map (sum(_))
          val hrss = hrs.sum
          val hd = x map (xi => (xi map (_.toDouble)) *:* (d*2.0))
          val hds = hd map (sum(_))
          val hdss = hds.sum 
          val h0 = hrss + hdss
          val t = if ((h0 < minH)|(h0>maxH)) 1e99
          else new Exponential(h0).draw
          if (t > dt) x else {
            if (Uniform(0.0,h0).draw < hdss) {
              // diffuse
              val j = Multinomial(DenseVector(hds.toArray)).sample // pick a box
              val i = Multinomial(hd(j)).sample // pick a species
              x(j)(i) = x(j)(i) - 1 // decrement chosen box
              if (Uniform(0.0, 1.0).draw < 0.5) {
                // left
                if (j > 0)
                  x(j-1)(i) = x(j-1)(i) + 1
                else
                  x(nv-1)(i) = x(nv-1)(i) + 1
              } else {
                // right
                if (j < nv-1)
                  x(j+1)(i) = x(j+1)(i) + 1
                else
                  x(0)(i) = x(0)(i) + 1
              }
              go(x, t0 + t, dt - t)
            } else {
              // react
              val j = Multinomial(DenseVector(hrs.toArray)).sample // pick a box
              val i = Multinomial(hr(j)).sample // pick a reaction
              go(x.updated(j,x(j) + Sto(::,i)), t0 + t, dt - t)
            }
          }
        }
      }
      go(x0, t0, dt)
    }
  }



}

// eof

