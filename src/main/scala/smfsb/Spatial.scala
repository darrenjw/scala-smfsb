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
  /**
    * The 1d spatial Gillespie algorithm
    * @param n A `Spn[IntState]` model for simulation
    * @param d A vector of diffusion coefficients - one for each species
    * @param minH Threshold for treating hazard as zero
    * @param maxH Threshold for terminating simulation early
    * 
    * @return A function with type signature `(x0: GenSeq[IntState], t0: Time, deltat: Time) => GenSeq[IntState]`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and initial time `t0`
    */
  def gillespie1d(
    n: Spn[IntState],
    d: DoubleState,
    minH: Double = 1e-20,
    maxH: Double = 1e6
  ): (GenSeq[IntState], Time, Time) => GenSeq[IntState] = {
    val Sto = (n.post - n.pre).t
    val u = Sto.rows // number of species
    assert(d.length == u)
      (x0: GenSeq[IntState], t0, dt) => {
      val nv = x0.length
      @tailrec
      def go(x: GenSeq[IntState], t0: Time, dt: Time): GenSeq[IntState] = {
        if (dt <= 0.0) x else {
          val hr = x map (n.h(_, t0))
          val hrs = hr map (sum(_))
          val hrss = hrs.sum
          val hd = x map (xi => (xi map (_.toDouble)) *:* (d*2.0))
          val hds = hd map (sum(_))
          val hdss = hds.sum 
          val h0 = hrss + hdss
          val t = if ((h0 < minH)|(h0 > maxH)) 1e99
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
              go(x.updated(j, x(j) + Sto(::,i)), t0 + t, dt - t)
            }
          }
        }
      }
      go(x0 map (_.copy), t0, dt)
    }
  }


  // TODO: Selective hazard recalculation, etc. Currently _very_ inefficient...
  /**
    * The 2d spatial Gillespie algorithm
    * @param n A `Spn[IntState]` model for simulation
    * @param d A vector of diffusion coefficients - one for each species
    * @param minH Threshold for treating hazard as zero
    * @param maxH Threshold for terminating simulation early
    * 
    * @return A function with type signature `(x0: PMatrix[IntState], t0: Time, deltat: Time) => PMatrix[IntState]`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and initial time `t0`
    */
  def gillespie2d(
    n: Spn[IntState],
    d: DoubleState,
    minH: Double = 1e-20,
    maxH: Double = 1e6
  ): (PMatrix[IntState], Time, Time) => PMatrix[IntState] = {
    val Sto = (n.post - n.pre).t
    val u = Sto.rows // number of species
      (x0: PMatrix[IntState], t0, dt) => {
      @tailrec
      def go(x: PMatrix[IntState], t0: Time, dt: Time): PMatrix[IntState] = {
        if (dt <= 0.0) x else {
          val hr = x map (n.h(_, t0))
          val hrs = hr map (sum(_))
          val hrss = hrs.data.sum
          val hd = x map (xi => (xi map (_.toDouble)) *:* (d*4.0))
          val hds = hd map (sum(_))
          val hdss = hds.data.sum 
          val h0 = hrss + hdss
          val t = if ((h0 < minH)|(h0 > maxH)) 1e99
          else new Exponential(h0).draw
          if (t > dt) x else {
            if (Uniform(0.0,h0).draw < hdss) {
              // diffuse
              val l = Multinomial(DenseVector(hds.data.toArray)).sample // pick a box
              val i = l / hds.r
              val j = l % hds.r
              val k = Multinomial(hd(i,j)).sample // pick a species
              x(i,j)(k) = x(i,j)(k) - 1 // decrement chosen box
              val un = Uniform(0.0, 1.0).draw
              if (un < 0.25) {
                // up
                if (j > 0)
                  x(i, j - 1)(k) = x(i, j - 1)(k) + 1
                else
                  x(i, x.r - 1)(k) = x(i, x.r - 1)(k) + 1
              } else if (un < 0.5) {
                // down
                if (j < x.r - 1)
                  x(i, j + 1)(k) = x(i, j + 1)(k) + 1
                else
                  x(i, 0)(k) = x(i, 0)(k) + 1
              } else if (un < 0.75) {
                // left
                if (i > 0)
                  x(i - 1, j)(k) = x(i - 1, j)(k) + 1
                else
                  x(x.c - 1, j)(k) = x(x.c - 1, j)(k) + 1
              } else {
                // right
                if (i < x.c - 1)
                  x(i + 1, j)(k) = x(i + 1, j)(k) + 1
                else
                  x(0, j)(k) = x(0, j)(k) + 1
              }
              go(x, t0 + t, dt - t)
            } else {
              // react
              val l = Multinomial(DenseVector(hrs.data.toArray)).sample // pick a box
              val i = l / hrs.r
              val j = l % hrs.r
              val k = Multinomial(hr(i,j)).sample // pick a reaction
              go(x.updated(i, j, x(i, j) + Sto(::,k)), t0 + t, dt - t)
            }
          }
        }
      }
      go(x0 map (_.copy), t0, dt)
    }
  }


  /**
    * The 1d spatial CLE algorithm
    * @param n A `Spn[DoubleState]` model for simulation
    * @param d A vector of diffusion coefficients - one for each species
    * @param dt Time step of the simulation algorithm
    * 
    * @return A function with type signature `(x0: GenSeq[DoubleState], t0: Time, deltat: Time) => GenSeq[DoubleState]`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and initial time `t0`
    */
  def cle1d(
    n: Spn[DoubleState],
    d: DoubleState,
    dt: Double = 0.01
  ): (GenSeq[DoubleState], Time, Time) => GenSeq[DoubleState] = {
    val Sto = (n.post - n.pre).t map (_.toDouble)
    val u = Sto.rows // number of species
    val v = Sto.cols // number of reactions
    val sdt = math.sqrt(dt)
    val sd = sqrt(d)
    assert(d.length == u)
    def laplacian(x: PVector[DoubleState]): DoubleState =
      x.forward.extract + x.back.extract + x.extract*(-2.0)
    def diffuse(x: PVector[DoubleState]): PVector[DoubleState] = {
      val noise = x map (s => s map (si => Gaussian(0.0, sdt).draw))
      val xn = x zip noise
      xn coflatMap (xnc => {
        val xc = xnc map (_._1)
        val nc = xnc map (_._2)
        val nx = xc.extract + (laplacian(xc) *:* (d*dt)) + sd *:* (
          (sqrt(xc.extract + xc.forward.extract) *:* noise.extract) -
            (sqrt(xc.extract + xc.back.extract) *:* noise.back.extract))
        abs(nx) // TODO: switch to rectification?
      })
    }
    // returned function closure
      (x0: GenSeq[DoubleState], t0, deltat) => {
      val nv = x0.length
      @tailrec
      def go(x: PVector[DoubleState], t0: Time, deltat: Time): GenSeq[DoubleState] = {
        if (deltat <= 0.0) x.vec else {
          val x2 = diffuse(x)
          val x3 = x2 map (xx => {
            val hr = n.h(xx, t0)
            val dwt = DenseVector(Gaussian(0.0, sdt).sample(v).toArray)
            val nx = xx + Sto * (hr*dt + sqrt(hr) *:* dwt)
            abs(nx)
          })
          go(x3, t0 + dt, deltat - dt)
        }
      }
      go(PVector(0,x0.toVector.par), t0, deltat)
    }
  }


  /**
    * The 1d spatial Euler algorithm
    * @param n A `Spn[DoubleState]` model for simulation
    * @param d A vector of diffusion coefficients - one for each species
    * @param dt Time step of the simulation algorithm
    * 
    * @return A function with type signature `(x0: GenSeq[DoubleState], t0: Time, deltat: Time) => GenSeq[DoubleState]`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and initial time `t0`
    */
  def euler1d(
    n: Spn[DoubleState],
    d: DoubleState,
    dt: Double = 0.01
  ): (GenSeq[DoubleState], Time, Time) => GenSeq[DoubleState] = {
    val Sto = (n.post - n.pre).t map (_.toDouble)
    val u = Sto.rows // number of species
    val v = Sto.cols // number of reactions
    val sdt = math.sqrt(dt)
    val sd = sqrt(d)
    assert(d.length == u)
    def laplacian(x: PVector[DoubleState]): DoubleState =
      x.forward.extract + x.back.extract + x.extract*(-2.0)
    def diffuse(x: PVector[DoubleState]): PVector[DoubleState] = {
      x coflatMap (xc => {
        val nx = xc.extract + (laplacian(xc) *:* (d*dt))
        abs(nx) // TODO: switch to rectification?
      })
    }
    // returned function closure
      (x0: GenSeq[DoubleState], t0, deltat) => {
      val nv = x0.length
      @tailrec
      def go(x: PVector[DoubleState], t0: Time, deltat: Time): GenSeq[DoubleState] = {
        if (deltat <= 0.0) x.vec else {
          val x2 = diffuse(x)
          val x3 = x2 map (xx => {
            val hr = n.h(xx, t0)
            val nx = xx + Sto * (hr*dt)
            abs(nx)
          })
          go(x3, t0 + dt, deltat - dt)
        }
      }
      go(PVector(0,x0.toVector.par), t0, deltat)
    }
  }



  /**
    * The 2d spatial CLE algorithm
    * @param n A `Spn[DoubleState]` model for simulation
    * @param d A vector of diffusion coefficients - one for each species
    * @param dt Time step of the simulation algorithm
    * 
    * @return A function with type signature `(x0: PMatrix[DoubleState], t0: Time, deltat: Time) => PMatrix[DoubleState]`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and initial time `t0`
    */
  def cle2d(
    n: Spn[DoubleState],
    d: DoubleState,
    dt: Double = 0.01
  ): (PMatrix[DoubleState], Time, Time) => PMatrix[DoubleState] = {
    val Sto = (n.post - n.pre).t map (_.toDouble)
    val u = Sto.rows // number of species
    val v = Sto.cols // number of reactions
    val sdt = math.sqrt(dt)
    val sd = sqrt(d)
    assert(d.length == u)
    def laplacian(x: PMatrix[DoubleState]): DoubleState =
      x.left.extract + x.right.extract + x.up.extract + x.down.extract + x.extract*(-4.0)
    def rectify(x: DoubleState): DoubleState = x map (xi => if (xi > 0.0) xi else 0.0)
    def diffuse(x: PMatrix[DoubleState]): PMatrix[DoubleState] = {
      val noise1 = x map (s => s map (si => Gaussian(0.0, sdt).draw))
      val noise2 = x map (s => s map (si => Gaussian(0.0, sdt).draw))
      val xn = x zip (noise1 zip noise2)
      xn coflatMap (xnc => {
        val xc = xnc map (_._1)
        val dwt = xnc map (_._2._1)
        val dwts = xnc map (_._2._2)
        val nx = xc.extract + (laplacian(xc) *:* (d*dt)) + sd *:* (
          (sqrt(xc.extract + xc.left.extract) *:* dwt.extract) -
            (sqrt(xc.extract + xc.right.extract) *:* dwt.right.extract) +
            (sqrt(xc.extract + xc.up.extract) *:* dwts.extract) -
            (sqrt(xc.extract + xc.down.extract) *:* dwts.down.extract)
        )
        rectify(nx)
      })
    }
    // returned function closure
      (x0: PMatrix[DoubleState], t0, deltat) => {
      @tailrec
      def go(x: PMatrix[DoubleState], t0: Time, deltat: Time): PMatrix[DoubleState] = {
        if (deltat <= 0.0) x else {
          val x2 = diffuse(x)
          val x3 = x2 map (xx => {
            val hr = n.h(xx, t0)
            val dwt = DenseVector(Gaussian(0.0, sdt).sample(v).toArray)
            val nx = xx + Sto * (hr*dt + (sqrt(hr) *:* dwt))
            rectify(nx)
          })
          go(x3, t0 + dt, deltat - dt)
        }
      }
      go(x0, t0, deltat)
    }
  }

    /**
    * The 2d spatial Euler algorithm
    * @param n A `Spn[DoubleState]` model for simulation
    * @param d A vector of diffusion coefficients - one for each species
    * @param dt Time step of the simulation algorithm
    * 
    * @return A function with type signature `(x0: PMatrix[DoubleState], t0: Time, deltat: Time) => PMatrix[DoubleState]`
    * which will simulate the state of the system at time `t0+deltat` given initial state `x0` and initial time `t0`
    */
  def euler2d(
    n: Spn[DoubleState],
    d: DoubleState,
    dt: Double = 0.01
  ): (PMatrix[DoubleState], Time, Time) => PMatrix[DoubleState] = {
    val Sto = (n.post - n.pre).t map (_.toDouble)
    val u = Sto.rows // number of species
    val v = Sto.cols // number of reactions
    val sdt = math.sqrt(dt)
    val sd = sqrt(d)
    assert(d.length == u)
    def laplacian(x: PMatrix[DoubleState]): DoubleState =
      x.left.extract + x.right.extract + x.up.extract + x.down.extract + x.extract*(-4.0)
    def rectify(x: DoubleState): DoubleState = x map (xi => if (xi > 0.0) xi else 0.0)
    def diffuse(x: PMatrix[DoubleState]): PMatrix[DoubleState] = {
      x coflatMap (xc => {
        val nx = xc.extract + (laplacian(xc) *:* (d*dt))
        rectify(nx)
      })
    }
    // returned function closure
      (x0: PMatrix[DoubleState], t0, deltat) => {
      @tailrec
      def go(x: PMatrix[DoubleState], t0: Time, deltat: Time): PMatrix[DoubleState] = {
        if (deltat <= 0.0) x else {
          val x2 = diffuse(x)
          val x3 = x2 map (xx => {
            val hr = n.h(xx, t0)
            val nx = xx + Sto * (hr*dt)
            rectify(nx)
          })
          go(x3, t0 + dt, deltat - dt)
        }
      }
      go(x0, t0, deltat)
    }
  }


  /**
    * Plot the output of a 1d time series simulation. Called solely for the side-effect 
    * of rendering a plot on the console.
    * 
    * @param ts Output from a 1d spatial time series simulation
    */
  def plotTs1d[S: State](ts: Ts[GenSeq[S]]): Unit = {
    import breeze.plot._
    val states = ts map (_._2)
      (0 until states(0)(0).toDvd.length).foreach{ i =>
        val f = Figure("Species " + i)
        val p = f.subplot(0)
        val speciesi = states map (statetime => statetime map (_.toDvd.data(i)))
        val vec = speciesi map (statetime => new DenseMatrix(statetime.length,1,statetime.toArray))
        val mat = vec.reduce((x,y) => DenseMatrix.horzcat(x,y))
        p += image(mat)
        p.xlabel = "Time"
        p.ylabel = "Space"
        f.saveas("TsPlot1d.png")
      }
  }

}

// eof

