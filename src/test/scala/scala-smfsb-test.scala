/*
scala-smfsb-test.scala

Some basic tests

*/

package smfsb

import org.scalatest._
import org.scalatest.junit._
import org.junit.runner.RunWith

import breeze.linalg._
import Types._

@RunWith(classOf[JUnitRunner])
class MyTestSuite extends FunSuite {

  test("1+2=3") {
    assert(1 + 2 === 3)
  }

  test("create and step LV model") {
    val model = SpnModels.lv[IntState]()
    val step = Step.gillespie(model)
    val output = step(DenseVector(50, 100), 0.0, 1.0)
    assert(output.length === 2)
  }

  test("create and step ID model") {
    val model = SpnModels.id[IntState]()
    val step = Step.gillespie(model)
    val output = step(DenseVector(0), 0.0, 1.0)
    assert(output.length === 1)
  }

  test("Sim.ts for LV model") {
    val model = SpnModels.lv[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.ts(DenseVector(50, 40), 0.0, 20.0, 0.1, step)
    // Sim.plotTs(ts)
    assert(ts.length === 201)
  }

  test("Sim.ts for MM model") {
    val model = SpnModels.mm[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.ts(DenseVector(301, 120, 0, 0), 0.0, 100.0, 1.0, step)
    //Sim.plotTs(ts)
    assert(ts.length === 101)
    assert(ts(0)._1 === 0.0)
    assert(ts(1)._1 === 1.0)
    assert(ts(0)._2.data(0) === 301)
    assert(ts(0)._2.data(1) === 120)
    assert(ts(0)._2.data(2) === 0)
    assert(ts(0)._2.data(3) === 0)
  }

  test("Sim.times for MM model") {
    val model = SpnModels.mm[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.times(DenseVector(301, 120, 0, 0), 0.0, List(0.0,10.0,20.0,50.0,100.0), step)
    //Sim.plotTs(ts)
    assert(ts.length === 5)
    assert(ts(0)._1 === 0.0)
    assert(ts(1)._1 === 10.0)
    assert(ts(0)._2.data(0) === 301)
    assert(ts(0)._2.data(1) === 120)
    assert(ts(0)._2.data(2) === 0)
    assert(ts(0)._2.data(3) === 0)
  }

  test("Sim.times for MM model (offset start)") {
    val model = SpnModels.mm[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.times(DenseVector(301, 120, 0, 0), 0.0, List(5.0,10.0,20.0,50.0,100.0), step)
    assert(ts.length === 5)
    assert(ts(0)._1 === 5.0)
    assert(ts(1)._1 === 10.0)
    assert(ts(0)._2.data(0) < 301)
    assert(ts(0)._2.data(3) > 0)
  }

  test("Sim.ts with pts for MM model") {
    val model = SpnModels.mm[IntState]()
    val step = Step.pts(model, 0.1)
    val ts = Sim.ts(DenseVector(301, 120, 0, 0), 0.0, 100.0, 0.5, step)
    // Sim.plotTs(ts)
    assert(ts.length === 201)
  }

  test("Sim.ts with CLE for LV model") {
    val model = SpnModels.lv[DoubleState]()
    val step = Step.cle(model, 0.01)
    val ts = Sim.ts(DenseVector(50.0, 40.0), 0.0, 20.0, 0.1, step)
    //Sim.plotTs(ts)
    assert(ts.length === 201)
    assert(ts(0)._1 === 0.0)
    assert(ts(1)._1 === 0.1)
    assert(ts(0)._2(0) === 50.0)
    assert(ts(0)._2(1) === 40.0)
  }

  test("Sim.ts with Euler for MM model") {
    val model = SpnModels.mm[DoubleState]()
    val step = Step.euler(model, 0.1)
    val ts = Sim.ts(DenseVector(301.0, 120.0, 0.0, 0.0), 0.0, 100.0, 0.5, step)
    //Sim.plotTs(ts)
    assert(ts.length === 201)
  }

  test("Sim.ts for AR model") {
    val model = SpnModels.ar[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.ts(DenseVector(10, 0, 0, 0, 0), 0.0, 500.0, 0.5, step)
    // Sim.plotTs(ts)
    assert(ts.length === 1001)
  }

  test("Sim.ts with pts for AR model") {
    val model = SpnModels.ar[IntState]()
    val step = Step.pts(model, 0.001)
    val ts = Sim.ts(DenseVector(10, 0, 0, 0, 0), 0.0, 500.0, 0.5, step)
    // Sim.plotTs(ts)
    assert(ts.length === 1001)
  }


}

/* eof */

