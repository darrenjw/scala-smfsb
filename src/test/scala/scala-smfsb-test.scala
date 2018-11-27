/*
scala-smfsb-test.scala

Some basic tests

*/

package smfsb

import org.scalatest._
import org.scalatest.junit._
import org.junit.runner.RunWith

import breeze.linalg._

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
    assert(ts(0)._2(0) === 301)
    assert(ts(0)._2(1) === 120)
    assert(ts(0)._2(2) === 0)
    assert(ts(0)._2(3) === 0)
  }

  test("Sim.times for MM model (offset start)") {
    val model = SpnModels.mm[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.times(DenseVector(301, 120, 0, 0), 0.0, List(5.0,10.0,20.0,50.0,100.0), step)
    assert(ts.length === 5)
    assert(ts(0)._1 === 5.0)
    assert(ts(1)._1 === 10.0)
    assert(ts(0)._2(0) < 301)
    assert(ts(0)._2(3) > 0)
  }

  test("Sim.sample for MM model") {
    val model = SpnModels.mm[IntState]()
    val step = Step.gillespie(model)
    val sim = Sim.sample(10, DenseVector(301, 120, 0, 0), 0.0, 10.0, step)
    //println(sim)
    assert(sim.length === 10)
    assert(sim(0)(0) < 301)
    assert(sim(0)(3) > 0)
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


  test("pfMll for LV model") {
    import breeze.stats.distributions.Gaussian
    val model = SpnModels.lv[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 1.0, step)
    // Sim.plotTs(ts)
    assert(ts.length === 21)
    val mll = Mll.pfMll(
      (p: DenseVector[Double]) => collection.immutable.Vector.fill(100)(DenseVector(50,100)),
      0.0,
      (p: DenseVector[Double]) => Step.gillespie(SpnModels.lv[IntState](p)),
      (p: DenseVector[Double]) => (s: IntState, o: IntState) => {
        Gaussian(s(0), 10.0).logPdf(o(0))+Gaussian(s(1), 10.0).logPdf(o(1))
      },
      ts
    )
    val mll1 = mll(DenseVector(1.0,0.005,0.6))
    //println(mll1)
    assert(mll1 < 0.0)
  }

  test("nextValue (for MH-MCMC) on toy target") {
    import breeze.stats.distributions._
    val kernel = Mcmc.nextValue(
      Gaussian(0.0,1.0).logPdf,
      (o: Double) => o + Uniform(-0.1,0.1).draw,
      (n: Double, o: Double) => 1.0,
      (p: Double) => 1.0
    ) _
    val next = kernel(0.0,Double.MinValue)
    assert (math.abs(next._1) < 1.0)
    assert(next._2 > Double.MinValue)
  }

  test("mhStream on toy target") {
    import breeze.stats.distributions._
    val s = Mcmc.mhStream(
      0.0,
      Gaussian(0.0,1.0).logPdf,
      (o: Double) => o + Uniform(-1.5,1.5).draw,
      (n: Double, o: Double) => 1.0,
      (p: Double) => 1.0
    )
    val n = 10000
    val out = s.drop(0).take(n)
    //println(out.take(20).toList)
    Mcmc.summary(Mcmc.toDMD(out map (d => DenseVector(d))), plt=false)
    assert(out.length === n)
    assert(math.abs(out.sum / n) < 0.2)
  }

  test("toDMD") {
    val s = List(DenseVector(1,2,3),DenseVector(4,5,6)).toStream
    val m = Mcmc.toDMD(s)
    assert(m.rows === 2)
    assert(m.cols === 3)
    assert(m === DenseMatrix((1.0,2.0,3.0),(4.0,5.0,6.0)))
  }

  test("ACF") {
    val v = List(2,3,2,3,3,4,4,5,6,5,4,3,2,3,5)
    val acf = Mcmc.acf(v map (_.toDouble), 3)
    println(acf)
    assert(acf.length == 4)
    assert(math.abs(acf(0) - 1.0) < 1e-6)
  }

}

/* eof */

