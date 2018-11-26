/*
Step.scala

Step functions for forward simulation

 */

package smfsb

import annotation.tailrec
import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._

/**
  * Functions which accept a `Spn` and return a function for simulating from the transition kernel of that model
  */
object Step {

  /**
    * The Gillespie algorithm, sometimes known as the direct method, or the stochastic simulation algorithm (SSA)
    * @param n A `Spn[IntState]` model
    * 
    * @return A function with type signature `(x0: IntState, t0: Time, deltat: Time) => IntState`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and intial time `t0`
    */
  def gillespie(n: Spn[IntState]): (IntState, Time, Time) => IntState = {
    val Sto = (n.post - n.pre).t
    (x: IntState, t0, dt) => {
      @tailrec
      def go(x: IntState, t0: Time, dt: Time): IntState = {
        if (dt <= 0.0) x else {
          val h = n.h(x, t0)
          val h0 = sum(h)
          val t = if ((h0 < 1e-20)|(h0 > 1e6)) 1e99
          else new Exponential(h0).draw
          if (t > dt) x else {
            val i = Multinomial(h).sample
            go(x + Sto(::, i), t0 + t, dt - t)
          }
        }
      }
      go(x, t0, dt)
    }
  }

  private def myPoisson(mean: Double): Int = if (mean < 250.0) {
    // should cope better with large means...
    Poisson(mean).sample
  } else {
    abs(round(Gaussian(mean, math.sqrt(mean)).draw)).toInt
  }

  /**
    * A Poisson time-stepping algorithm. Like a tau-leaping algorithm, but with fixed step sizes.
    * 
    * @param n A `Spn[IntState]` model
    * @param dt The internal time step of the algorithm. Not the same as the `deltat` of the returned transition kernel.
    * 
    * @return A function with type signature `(x0: IntState, t0: Time, deltat: Time) => IntState`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and intial time `t0`
    */
  def pts(n: Spn[IntState], dt: Double = 0.01): (IntState, Time, Time) => IntState = {
    val Sto = (n.post - n.pre).t
    val v = Sto.cols
    (x, t0, deltat) => {
      @tailrec
      def go(x: IntState, t0: Time, deltat: Time): IntState = {
        //println(x.toCsv+" | "+t0+" "+deltat)
        if (deltat <= 1.0e-8) x else {
          val adt = if (dt > deltat) deltat else dt
          val h = n.h(x, t0)
          val r = h map (hi => myPoisson(hi * adt))
          val nx = x + (Sto * r).toDenseVector
          val tnx = nx map (xi => abs(xi))
          go(tnx, t0 + adt, deltat - adt)
        }
      }
      go(x, t0, deltat)
    }
  }


  /**
    * An Euler-Maruyama simulation of a CLE approximation to the provided `Spn`.
    * 
    * @param n A `Spn[DoubleState]` model (note that the state must be continous)
    * @param dt The internal time step of the algorithm. Not the same as the `deltat` of the returned transition kernel.
    * 
    * @return A function with type signature `(x0: DoubleState, t0: Time, deltat: Time) => DoubleState`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and intial time `t0`
    */
  def cle(n: Spn[DoubleState], dt: Double = 0.01): (DoubleState, Time, Time) => DoubleState = {
    val Sto = ((n.post - n.pre) map { _ * 1.0 }).t
    val v = Sto.cols
    val sdt = Math.sqrt(dt)
    (x, t0, deltat) => {
      @tailrec
      def go(x: DoubleState, t0: Time, deltat: Time): DoubleState = {
        if (deltat <= 0.0) x else {
          val adt = if (dt > deltat) deltat else dt
          val sdt = Math.sqrt(adt)
          val h = n.h(x, t0)
          val dw = DenseVector(Gaussian(0.0, sdt).sample(v).toArray)
          val dx = Sto * ((h * adt) + (sqrt(h) *:* dw))
          val nx = x + dx.toDenseVector
          val tnx = abs(nx)
          go(tnx, t0 + adt, deltat - adt)
        }
      }
      go(x, t0, deltat)
    }
  }

  /**
    * A simple Euler integration of the continuous deterministic approximation to the provided `Spn`.
    * Euler methods are well-known to be very unstable, but the function can be useful for getting
    * a basic idea of how the model behaves in the absence of noise.
    * 
    * @param n A `Spn[DoubleState]` model (note that the state must be continous)
    * @param dt The internal time step of the algorithm. Not the same as the `deltat` of the returned transition kernel.
    * 
    * @return A function with type signature `(x0: DoubleState, t0: Time, deltat: Time) => DoubleState`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and intial time `t0`
    */
  def euler(n: Spn[DoubleState], dt: Double = 0.01): (DoubleState, Time, Time) => DoubleState = {
    val Sto = ((n.post - n.pre) map { _ * 1.0 }).t
    val v = Sto.cols
    (x, t0, deltat) => {
      @tailrec
      def go(x: DoubleState, t0: Time, deltat: Time): DoubleState = {
        if (deltat <= 0.0) x else {
          val adt = if (dt > deltat) deltat else dt
          val sdt = Math.sqrt(adt)
          val h = n.h(x, t0)
          val dx = Sto * (h * adt)
          val nx = x + dx.toDenseVector
          val tnx = abs(nx)
          go(tnx, t0 + adt, deltat - adt)
        }
      }
      go(x, t0, deltat)
    }
  }




} // object Step


// eof
