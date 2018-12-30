/*
Cle2dfine.scala

Approximate simulation from a 2d reaction-diffusion master equation model using the CLE on a fine grid
Essentially numerically integrating a SPDE

 */

import smfsb._
import breeze.linalg.{Vector => BVec, _}
import breeze.numerics._
import breeze.plot._

object Cle2dfine {

  def main(args: Array[String]): Unit = {
    println("2d approximate spatial CLE simulation from a reaction-diffusion Lotka-Volterra model - will run FOREVER - just kill it when you get bored!")
    val r = 80
    val c = 100
    val model = SpnModels.lv[DoubleState]()
    val step = Spatial.cle2d(model, DenseVector(0.5, 0.5), 0.1)
    val x00 = DenseVector(0.0, 0.0)
    val x0 = DenseVector(50.0, 100.0)
    val xx00 = PMatrix(r, c, Vector.fill(r*c)(x00))
    val xx0 = xx00.updated(c/2, r/2, x0)

    // create an infinite stream of states
    val s = Stream.iterate(xx0)(x => step(x, 0.0, 0.5))

    // animate computation of the stream
    val f = Figure("2d spatial CLE simulation of the LV model")
    s.foreach(x => {
      f.clear
      f.width = 500
      f.height = 800
      val p0 = f.subplot(2, 1, 0)
      p0 += image(PMatrix.toBDM(x map (_.data(0))))
      p0.title = "Prey"
      val p1 = f.subplot(2, 1, 1)
      p1 += image(PMatrix.toBDM(x map (_.data(1))))
      p1.title = "Predator"
      f.refresh
    })

    println("Finished")
  }


}



// eof

