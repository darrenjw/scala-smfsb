/*
Gillespie2d.scala

Exact simulation from a 2d reaction-diffusion master equation model

 */

import smfsb._
import breeze.linalg.{Vector => BVec, _}
import breeze.numerics._
import breeze.plot._

object Gillespie2d {

  def main(args: Array[String]): Unit = {
    println("2d exact Gillespie simulation from an RDME model - will take a while...")
    val r = 20
    val c = 30
    val model = SpnModels.lv[IntState]()
    val step = Spatial.gillespie2d(model, DenseVector(0.6, 0.6))
    val x00 = DenseVector(0, 0)
    val x0 = DenseVector(50, 100)
    val xx00 = PMatrix(r, c, Vector.fill(r*c)(x00))
    val xx0 = xx00.updated(c/2, r/2, x0)
    val output = step(xx0, 0.0, 9.0)
    println(output)
    val f = Figure("LV")
    val p0 = f.subplot(2, 1, 0)
    p0 += image(PMatrix.toBDM(output map (_.data(0).toDouble)))
    val p1 = f.subplot(2, 1, 1)
    p1 += image(PMatrix.toBDM(output map (_.data(1).toDouble)))
    println("Finished")
  }


}



// eof


