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
  // TODO: doc for gillespie1d
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
              go(x.updated(j, x(j) + Sto(::,i)), t0 + t, dt - t)
            }
          }
        }
      }
      go(x0, t0, dt)
    }
  }


  // TODO: doc for cle1d
  def cle1d(
    n: Spn[DoubleState],
    d: DoubleState,
    dt: Double = 0.01
  ): (GenSeq[DoubleState], Time, Time) => GenSeq[DoubleState] = {
    val Sto = ((n.post - n.pre).t) map (_.toDouble)
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
            val nx = xx + (Sto * (hr*dt + sqrt(hr) *:* dwt))
            abs(nx)
          })
          go(x3, t0 + dt, deltat - dt)
        }
      }
      go(PVector(0,x0.toVector.par), t0, deltat)
    }
  }


  def plotTs1d[S: State](ts: Ts[GenSeq[S]]): Unit = {
    import breeze.plot._
    val states = ts map (_._2)
      (0 until states(0)(0).toDvd.length).foreach{ i =>
        val f = Figure("Species "+i)
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

