/*
Tutorial.scala

Stuff for the tutorial walk-through

 */

object Tutorial {

  def main(args: Array[String]): Unit = {

// ***************************************************************
    println("Start")

    import smfsb._
    import breeze.linalg._
    import breeze.numerics._

    // Simulate LV with Gillespie
    val model = SpnModels.lv[IntState]()
    val step = Step.gillespie(model)
    val ts = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, step)
    Sim.plotTs(ts, "Gillespie simulation of LV model with default parameters")

    // Simulate LV with non-default parameters
    val model2 = SpnModels.lv[IntState](DenseVector(1.0, 0.006, 0.3))
    val step2 = Step.gillespie(model2)
    val ts2 = Sim.ts(DenseVector(50, 40), 0.0, 50.0, 0.1, step2)
    Sim.plotTs(ts2, "Gillespie simulation of LV model with non-default parameters")

    // Simulate other models with Gillespie
    val stepMM = Step.gillespie(SpnModels.mm[IntState]())
    val tsMM = Sim.ts(DenseVector(301,120,0,0), 0.0, 100.0, 0.5, stepMM)
    Sim.plotTs(tsMM, "Gillespie simulation of the MM model")

    val stepID = Step.gillespie(SpnModels.id[IntState]())
    val tsID = Sim.ts(DenseVector(0), 0.0, 40.0, 0.1, stepID)
    Sim.plotTs(tsID, "Gillespie simulation of the ID model")

    val stepAR = Step.gillespie(SpnModels.ar[IntState]())
    val tsAR = Sim.ts(DenseVector(10, 0, 0, 0, 0), 0.0, 500.0, 0.5, stepAR)
    Sim.plotTs(tsAR, "Gillespie simulation of the AR model")

    // Simulate on an irregular time grid
    val tsi = Sim.times(DenseVector(50,100), 0.0, List(0.0,2.0,5.0,10.0,20.0), step)
    Sim.plotTs(tsi, "Simulation on an irregular time grid")

    // Simulate a sample
    val samp = Sim.sample(1000, DenseVector(50,100), 0.0, 10.0, step)
    import breeze.plot._
    val fig = Figure("Marginal of transition kernel")
    fig.subplot(1,2,0) += hist(DenseVector(samp.map(_.data(0)).toArray))
    fig.subplot(1,2,1) += hist(DenseVector(samp.map(_.data(1)).toArray))

    // Simulate LV with other algorithms
    val stepPts = Step.pts(model)
    val tsPts = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, stepPts)
    Sim.plotTs(tsPts, "Poisson time-step simulation of the LV model")

    val stepCle = Step.cle(SpnModels.lv[DoubleState]())
    val tsCle = Sim.ts(DenseVector(50.0, 100.0), 0.0, 20.0, 0.05, stepCle)
    Sim.plotTs(tsCle, "Euler-Maruyama/CLE simulation of the LV model")

    val stepE = Step.euler(SpnModels.lv[DoubleState]())
    val tsE = Sim.ts(DenseVector(50.0, 100.0), 0.0, 20.0, 0.05, stepE)
    Sim.plotTs(tsE, "Continuous-deterministic Euler simulation of the LV model")

    // Model building
    val mylv0 = UnmarkedSpn[IntState](
      List("x", "y"),
      DenseMatrix((1, 0), (1, 1), (0, 1)),
      DenseMatrix((2, 0), (0, 2), (0, 0)),
      (x, t) => {
        DenseVector(
          x(0) * 1.0, x(0) * x(1) * 0.005, x(1) * 0.6
        )}
    )
    val ts0 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(mylv0))
    Sim.plotTs(ts0, "Gillespie simulation of LV0")

    def lvparam(p: DenseVector[Double] = DenseVector(1.0, 0.005, 0.6)): Spn[IntState] =
    UnmarkedSpn[IntState](
      List("x", "y"),
      DenseMatrix((1, 0), (1, 1), (0, 1)),
      DenseMatrix((2, 0), (0, 2), (0, 0)),
      (x, t) => {
        DenseVector(
          x.data(0) * p(0), x.data(0) * x.data(1) * p(1), x.data(1) * p(2)
        )}
    )

    val mylv1 = lvparam(DenseVector(1.0, 0.005, 0.6))
    val tslv1 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(mylv1))
    Sim.plotTs(tslv1, "Gillespie simulation of LV1")

    val mylv2 = lvparam(DenseVector(1.1, 0.01, 0.6))
    val tslv2 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(mylv2))
    Sim.plotTs(tslv2, "Gillespie simulation of LV2")

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


    val mylv3: Spn[IntState] = lvparam() // here due to compiler bug!
    val tslv3 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(mylv3))
    Sim.plotTs(tslv3, "Gillespie simulation of LV3")


    val lvDiscrete = lv[IntState]()
    val tsDiscrete = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(lvDiscrete))
    Sim.plotTs(tsDiscrete, "Gillespie simulation of lvDiscrete")

    val lvDiscrete2 = lv[IntState](DenseVector(1.1, 0.01, 0.6))
    val tsDiscrete2 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(lvDiscrete2))
    Sim.plotTs(tsDiscrete2, "Gillespie simulation of lvDiscrete2")

    val lvCts = lv[DoubleState]()
    val tsCts = Sim.ts(DenseVector(50.0, 100.0), 0.0, 20.0, 0.05, Step.cle(lvCts))
    Sim.plotTs(tsCts, "Gillespie simulation of lvCts")

    println("End")
// ***************************************************************

  }

}

// eof

