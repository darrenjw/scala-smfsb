/*
examples-test.scala

Some basic tests

*/

package smfsb

import org.scalatest._
import org.scalatest.junit._
// import org.scalatest.prop._
import org.junit.runner.RunWith

import breeze.linalg._
import Types._

@RunWith(classOf[JUnitRunner])
class MyTestSuite extends FunSuite {


  test("pfMll creation and evaluation") {
    import LvPmmh._
    import SpnExamples._
    import Mll._
    import scala.io.Source
    val rawData = Source.fromFile("../LVpreyNoise10.txt").getLines
    val data = ((0 to 30 by 2).toList zip rawData.toList) map { x => (x._1.toDouble, DenseVector(x._2.toDouble)) }
    val mll = pfMll(160, simPrior, 0.0, stepLv, obsLik, data)
    val mlle = mll(lvparam)
    // println(mlle)
    assert(mlle < 0.0)
  }

  test("Parallel pfMll creation and evaluation") {
    import LvPmmh._
    import SpnExamples._
    import Mll._
    import scala.io.Source
    val rawData = Source.fromFile("../LVpreyNoise10.txt").getLines
    val data = ((0 to 30 by 2).toList zip rawData.toList) map { x => (x._1.toDouble, DenseVector(x._2.toDouble)) }
    val mll = pfMllP(160, simPrior, 0.0, stepLv, obsLik, data)
    val mlle = mll(lvparam)
    // println(mlle)
    assert(mlle < 0.0)
  }

  test("Serial and parallel pfMll should be similar") {
    import LvPmmh._
    import SpnExamples._
    import Mll._
    import scala.io.Source
    val rawData = Source.fromFile("../LVpreyNoise10.txt").getLines
    val data = ((0 to 30 by 2).toList zip rawData.toList) map { x => (x._1.toDouble, DenseVector(x._2.toDouble)) }
    val mll = pfMll(320, simPrior, 0.0, stepLv, obsLik, data)
    val mllp = pfMllP(320, simPrior, 0.0, stepLv, obsLik, data)
    val mlle = mll(lvparam)
    val mllep = mllp(lvparam)
    //println(mlle+" "+mllep)
    assert(math.abs(mlle-mllep) < 2.0)
  }




}

