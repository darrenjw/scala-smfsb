/*
Cle2d.scala

Approximate simulation from a 2d reaction-diffusion master equation model using the CLE

 */

import smfsb.*
import breeze.linalg.{Vector => BVec, *}
import breeze.numerics.*
import breeze.plot.*

@main def cle2d() =
  println("2d approximate CLE simulation from an RDME model...")
  val r = 200
  val c = 300
  val model = SpnModels.lv[DoubleState]()
  val step = Spatial.cle2d(model, DenseVector(0.6, 0.6), 0.05)
  val x00 = DenseVector(0.0, 0.0)
  val x0 = DenseVector(50.0, 100.0)
  val xx00 = PMatrix(r, c, Vector.fill(r * c)(x00))
  val xx0 = xx00.updated(c / 2, r / 2, x0)
  val output = step(xx0, 0.0, 15.0)
  val f = Figure("LV")
  val p0 = f.subplot(2, 1, 0)
  p0 += image(PMatrix.toBDM(output map (_.data(0))))
  val p1 = f.subplot(2, 1, 1)
  p1 += image(PMatrix.toBDM(output map (_.data(1))))
  println("Finished")

// eof
