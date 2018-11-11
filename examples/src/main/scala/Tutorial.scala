/*
Tutorial.scala

Stuff for the tutorial walk-through

 */

object Tutorial {

  def main(args: Array[String]): Unit = {

// ***************************************************************
    println("Start")

    import smfsb._
    import smfsb.Types._
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
    Sim.plotTs(tsAR, "Gillespie simulation of the ID model")

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

    // PMCMC-based parameter inference in other example

    println("End")
// ***************************************************************

  }

}

// eof

