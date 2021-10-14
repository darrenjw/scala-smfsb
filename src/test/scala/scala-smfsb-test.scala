/*
scala-smfsb-test.scala

Some basic tests

*/

package smfsb

import org.scalatest._
import funsuite._
import scala.collection.parallel.immutable.ParSeq
import scala.collection.parallel.CollectionConverters._

import breeze.stats.distributions.Rand.VariableSeed.randBasis

import breeze.linalg.{Vector => BVec, _}

class MyTestSuite extends AnyFunSuite {

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

  test("Sim.ts for LV4 model") {
    val model = SpnModels.lv4[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.ts(DenseVector(50, 40), 0.0, 20.0, 0.1, step)
    // Sim.plotTs(ts)
    assert(ts.length === 201)
  }

  test("Sim.ts for SIR model") {
    val model = SpnModels.sir[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.ts(DenseVector(100, 5, 0), 0.0, 20.0, 0.1, step)
    // Sim.plotTs(ts)
    assert(ts.length === 201)
  }

  test("Sim.ts for SEIR model") {
    val model = SpnModels.seir[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.ts(DenseVector(100, 5, 0, 0), 0.0, 20.0, 0.1, step)
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
      (p: DenseVector[Double]) => Vector.fill(100)(DenseVector(50,100)),
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
      (o: Double) => o + Uniform(-0.1,0.1).draw(),
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
      (o: Double) => o + Uniform(-1.5,1.5).draw(),
      (n: Double, o: Double) => 1.0,
      (p: Double) => 1.0
    )
    val n = 10000
    val out = s.drop(0).take(n)
    Mcmc.summary(Mcmc.toDMD(out map (d => DenseVector(d))), plt=false)
    assert(out.length === n)
    assert(math.abs(out.sum / n) < 0.2)
  }

  test("toDMD") {
    val s = LazyList(DenseVector(1,2,3),DenseVector(4,5,6))
    val m = Mcmc.toDMD(s)
    assert(m.rows === 2)
    assert(m.cols === 3)
    assert(m === DenseMatrix((1.0,2.0,3.0),(4.0,5.0,6.0)))
  }

  test("ACF") {
    val v = List(2,3,2,3,3,4,4,5,6,5,4,3,2,3,5)
    val acf = Mcmc.acf(v map (_.toDouble), 3)
    //println(acf)
    assert(acf.length == 4)
    assert(math.abs(acf(0) - 1.0) < 1e-6)
  }

  test("create and step LV model in 1d") {
    val model = SpnModels.lv[IntState]()
    val step = Spatial.gillespie1d(model,DenseVector(0.1,0.1))
    val x00 = DenseVector(0,0)
    val x0 = DenseVector(50,100)
    val xx00 = Vector.fill(10)(x00)
    val xx0 = xx00.updated(5,x0)
    val output = step(xx0, 0.0, 1.0)
    //println(output)
    assert(output.length === 10)
    assert(output(0).length === 2)
  }

  test("simulate a time series for the LV model in 1d") {
    //val N = 20
    //val T = 30.0
    val N = 10
    val T = 20.0
    val model = SpnModels.lv[IntState]()
    val step = Spatial.gillespie1d(model,DenseVector(0.6,0.6))
    val x00 = DenseVector(0,0)
    val x0 = DenseVector(50,100)
    val xx00 = Vector.fill(N)(x00)
    val xx0 = xx00.updated(N/2,x0)
    val output = Sim.ts(xx0, 0.0, T, 0.2, step)
    //Spatial.plotTs1d(output)
    assert(output.length === (T/0.2).toInt + 2)
    assert(output(0)._2.length === N)
  }

  test("PVector") {
    val v = PVector(0,List(1,2,3).toVector.par)
    assert(v.length === 3)
    assert(v.cur === 0)
    assert(v.extract === 1)
    val vf = v.forward
    assert(vf.length === 3)
    assert(vf.cur === 1)
    assert(vf.extract === 2)
    val vb = v.back
    assert(vb.length === 3)
    assert(vb.cur === 2)
    assert(vb.extract === 3)
    val vbf = vb.forward
    assert (vbf === v)
    val vcf = v.coflatMap(vc => vc.extract + vc.forward.extract)
    assert(vcf.length === 3)
    assert(vcf.cur === 0)
    assert(vcf.extract === 3)
    assert(vcf.forward.extract === 5)
    assert(vcf.back.extract === 4)
  }

  test("PMatrix") {
    val m = PMatrix(0, 0, 2, 3, (1 to 6).toVector.par)
    assert(m.r*m.c === m.data.length)
    assert(m(2,1) === 6)
    assert(m.up.down === m)
    assert(m.down.up === m)
    assert(m.left.right === m)
    assert(m.right.left === m)
    assert(m.extract === 1)
    assert(m.down.extract === 2)
    assert(m.up.extract === 2)
    assert(m.right.extract === 3)
    assert(m.left.extract === 5)
    val mcf = m.coflatMap(mc => mc.extract + mc.right.extract)
    assert(mcf.extract === 4)
    assert(mcf.right.extract === 8)
    assert(mcf.down.extract === 6)
    assert(mcf.left.extract === 6)
  }

  test("PMatrix utilities") {
    val bm = DenseMatrix((1,2,3),(4,5,6))
    assert(bm.rows === 2)
    assert(bm.cols === 3)
    assert(bm(0,0) === 1)
    assert(bm(0,1) === 2)
    assert(bm(1,2) === 6)
    val pm = PMatrix.fromBDM(bm)
    assert(pm.r === 2)
    assert(pm.c === 3)
    assert(pm(0,0) === 1)
    assert(pm(1,0) === 2)
    assert(pm(2,1) === 6)
    val bdmd = PMatrix.toBDM(pm map (_.toDouble))
    assert(bdmd === bm.map(_.toDouble))
  }

  test("create and step LV model in 1d with the CLE") {
    val model = SpnModels.lv[DoubleState]()
    val step = Spatial.cle1d(model,DenseVector(0.1, 0.1))
    val x00 = DenseVector(0.0, 0.0)
    val x0 = DenseVector(50.0, 100.0)
    val xx00 = Vector.fill(10)(x00)
    val xx0 = xx00.updated(5, x0)
    val output = step(xx0, 0.0, 1.0)
    //println(output)
    assert(output.length === 10)
    assert(output(0).length === 2)
  }

  test("simulate a time series for the LV model in 1d with the CLE") {
    val N = 25
    val T = 30.0
    val model = SpnModels.lv[DoubleState]()
    val step = Spatial.cle1d(model, DenseVector(0.6, 0.6), 0.05)
    val x00 = DenseVector(0.0, 0.0)
    val x0 = DenseVector(50.0, 100.0)
    val xx00 = Vector.fill(N)(x00)
    val xx0 = xx00.updated(N/2, x0)
    val output = Sim.ts(xx0, 0.0, T, 0.2, step)
    //Spatial.plotTs1d(output)
    assert(output.length === (T/0.2).toInt + 2)
    assert(output(0)._2.length === N)
  }

  test("simulate a time series for the LV model in 1d with the Euler method") {
    val N = 25
    val T = 30.0
    val model = SpnModels.lv[DoubleState]()
    val step = Spatial.euler1d(model, DenseVector(0.6, 0.6), 0.05)
    val x00 = DenseVector(0.0, 0.0)
    val x0 = DenseVector(50.0, 100.0)
    val xx00 = Vector.fill(N)(x00)
    val xx0 = xx00.updated(N/2, x0)
    val output = Sim.ts(xx0, 0.0, T, 0.2, step)
    //Spatial.plotTs1d(output)
    assert(output.length === (T/0.2).toInt + 2)
    assert(output(0)._2.length === N)
  }

  test("create and step LV model in 2d") {
    val r = 5
    val c = 7
    val model = SpnModels.lv[IntState]()
    val step = Spatial.gillespie2d(model, DenseVector(0.6,0.6))
    val x00 = DenseVector(0, 0)
    val x0 = DenseVector(50, 100)
    val xx00 = PMatrix(r, c, Vector.fill(r*c)(x00))
    val xx0 = xx00.updated(c/2, r/2, x0)
    val output = step(xx0, 0.0, 1.0)
    /*
    println(output)
    import breeze.plot._
    val f = Figure("LV")
    val p0 = f.subplot(2,1,0)
    p0 += image(PMatrix.toBDM(output map (_(0).toDouble)))
    val p1 = f.subplot(2,1,1)
    p1 += image(PMatrix.toBDM(output map (_(1).toDouble)))
    */
    assert(output(0,0).length === 2)
  }

  test("create and step LV model in 2d with the CLE") {
    val r = 10
    val c = 15
    val model = SpnModels.lv[DoubleState]()
    val step = Spatial.cle2d(model, DenseVector(0.6,0.6), 0.05)
    val x00 = DenseVector(0.0, 0.0)
    val x0 = DenseVector(50.0, 100.0)
    val xx00 = PMatrix(r, c, Vector.fill(r*c)(x00))
    val xx0 = xx00.updated(c/2, r/2, x0)
    val output = step(xx0, 0.0, 8.0)
    /*
    println(output)
    import breeze.plot._
    val f = Figure("LV")
    val p0 = f.subplot(2,1,0)
    p0 += image(PMatrix.toBDM(output map (_(0))))
    val p1 = f.subplot(2,1,1)
    p1 += image(PMatrix.toBDM(output map (_(1))))
     */
    assert(output(0,0).length === 2)
  }

  test("create and step LV model in 2d with the Euler algorithm") {
    val r = 10
    val c = 15
    val model = SpnModels.lv[DoubleState]()
    val step = Spatial.euler2d(model, DenseVector(0.6,0.6), 0.05)
    val x00 = DenseVector(0.0, 0.0)
    val x0 = DenseVector(50.0, 100.0)
    val xx00 = PMatrix(r, c, Vector.fill(r*c)(x00))
    val xx0 = xx00.updated(c/2, r/2, x0)
    val output = step(xx0, 0.0, 8.0)
    /*
    import breeze.plot._
    val f = Figure("LV")
    val p0 = f.subplot(2,1,0)
    p0 += image(PMatrix.toBDM(output map (_(0))))
    val p1 = f.subplot(2,1,1)
    p1 += image(PMatrix.toBDM(output map (_(1))))
     */
    assert(output(0,0).length === 2)
  }


}

/* eof */

