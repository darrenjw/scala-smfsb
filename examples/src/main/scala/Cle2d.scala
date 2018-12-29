/*
Cle2d.scala

Approximate simulation from a 2d reaction-diffusion master equation model using the CLE

 */

import smfsb._
import breeze.linalg.{Vector => BVec, _}
import breeze.numerics._
import breeze.plot._

object Cle2d {

  def main(args: Array[String]): Unit = {
    println("2d approximate CLE simulation from an RDME model...")
    val r = 20
    val c = 30
    val model = SpnModels.lv[DoubleState]()
    val step = Spatial.cle2d(model, DenseVector(0.6, 0.6), 0.05)
    val x00 = DenseVector(0.0, 0.0)
    val x0 = DenseVector(50.0, 100.0)
    val xx00 = PMatrix(r, c, Vector.fill(r*c)(x00))
    val xx0 = xx00.updated(c/2, r/2, x0)
    val output = step(xx0, 0.0, 8.0)
    println(output)
    val f = Figure("LV")
    val p0 = f.subplot(2, 1, 0)
    p0 += image(PMatrix.toBDM(output map (_.data(0))))
    val p1 = f.subplot(2, 1, 1)
    p1 += image(PMatrix.toBDM(output map (_.data(1))))
    println("Finished")
  }


}



// eof


