/*
Spn.scala

Example SPN models

 */

package smfsb

import Types._
import breeze.linalg._

object SpnModels {

  def lv[S: State](p: DenseVector[Double] = DenseVector(1.0, 0.005, 0.6)): Spn[S] =
    UnmarkedSpn[S](
      List("x", "y"),
      DenseMatrix((1, 0), (1, 1), (0, 1)),
      DenseMatrix((2, 0), (0, 2), (0, 0)),
        (x, t) => {
          val xd = x.toDvd
          DenseVector(
        xd(0) * p(0), xd(0) * xd(1) * p(1), xd(1) * p(2)
      )}
      )

  def id[S: State](p: DenseVector[Double] = DenseVector(1.0, 0.1)): Spn[S] =
    UnmarkedSpn[S](
    List("X"),
    DenseMatrix((0), (1)),
    DenseMatrix((1), (0)),
      (x, t) => {
        val xd = x.toDvd
        DenseVector(p(0), xd(0) * p(1))
      }
    )

  def mm[S: State](p: DenseVector[Double] = DenseVector(0.00166, 1e-04, 0.1)): Spn[S] =
    UnmarkedSpn[S](
    List("S", "E", "SE", "P"),
    DenseMatrix((1, 1, 0, 0), (0, 0, 1, 0), (0, 0, 1, 0)),
    DenseMatrix((0, 0, 1, 0), (1, 1, 0, 0), (0, 1, 0, 1)),
      (x, t) => {
        val xd = x.toDvd
        DenseVector(p(0) * xd(0) * xd(1), p(1) * xd(2), p(2) * xd(2))
      }
  )

  def ar[S: State](c: DenseVector[Double] = DenseVector(1.0, 10.0, 0.01, 10.0, 1.0, 1.0, 0.1, 0.01)): Spn[S] =
    UnmarkedSpn[S](
    List("g.P2", "g", "r", "P", "P2"),
    DenseMatrix((0, 1, 0, 0, 1), (1, 0, 0, 0, 0), (0, 1, 0, 0, 0), (0, 0, 1, 0, 0), (0, 0, 0, 2, 0), (0, 0, 0, 0, 1), (0, 0, 1, 0, 0), (0, 0, 0, 1, 0)),
    DenseMatrix((1, 0, 0, 0, 0), (0, 1, 0, 0, 1), (0, 1, 1, 0, 0), (0, 0, 1, 1, 0), (0, 0, 0, 0, 1), (0, 0, 0, 2, 0), (0, 0, 0, 0, 0), (0, 0, 0, 0, 0)),
      (x, t) => {
        val xd = x.toDvd
        DenseVector(c(0) * xd(1) * xd(4), c(1) * xd(0), c(2) * xd(1), c(3) * xd(2), c(4) * 0.5 * xd(3) * (xd(3) - 1), c(5) * xd(4), c(6) * xd(2), c(7) * xd(3))
      }
  )

}

// eof
