/*
Abc.scala

Functions for ABC and ABC-SMC

*/

package smfsb

import Types._

/**
  * Functions for parameter inference using ABC (and ABC-SMC) methods
  */
object Abc {

  import scala.collection.GenSeq

  def run[P](n: Int, rprior: => P, dist: P => Double): GenSeq[(P, Double)] = {
    (1 to n).par.map(i => {
      val p = rprior
      (p,dist(p))
    })
  }




}

// eof

