/*
Gillespie1d.scala

Exact simulation from a 1d reaction-diffusion master equation model

 */

import smfsb._
import breeze.linalg.{Vector => BVec, _}
import breeze.numerics._

object Gillespie1d {


  def main(args: Array[String]): Unit = {
    println("1d exact simulation from an RDME model...")
    val N = 50
    val T = 40.0
    val model = SpnModels.lv[IntState]()
    val step = Spatial.gillespie1d(model,DenseVector(0.8, 0.8))
    val x00 = DenseVector(0, 0)
    val x0 = DenseVector(50, 100)
    val xx00 = Vector.fill(N)(x00)
    val xx0 = xx00.updated(N/2,x0)
    val output = Sim.ts(xx0, 0.0, T, 0.2, step)
    Spatial.plotTs1d(output)
    println("Finished")
  }


}



// eof


