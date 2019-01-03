# scala-smfsb tutorial

This tutorial document will walk through some basic features of the `scala-smfsb` library. Some familiarity with both Scala and the `smfsb` R package will be helpful, but is not strictly necessary. Note that the library relies on the Scala [Breeze](https://github.com/scalanlp/breeze/blob/master/README.md) library for linear algebra and probability distributions, so some familiarity with that library can also be helpful.

## Setup

To follow the tutorial, you need to have [Sbt](http://www.scala-sbt.org/) installed, and this in turn requires a recent [JDK](http://www.oracle.com/technetwork/java/javase/downloads). If you are new to Scala, you may find the [setup page](https://github.com/darrenjw/scala-course/blob/master/Setup.md) for my [Scala course](https://github.com/darrenjw/scala-course/blob/master/StartHere.md) to be useful, but note that on many Linux systems it can be as simple as installing the packages `openjdk-8-jdk` and `sbt`.

Once you have Sbt installed, you should be able to run it by entering `sbt` at your OS command line. You now need to use Sbt to create a Scala REPL with a dependency on the `scala-smfsb` library. There are many ways to do this, but if you are new to Scala, the simplest way is probably to start up Sbt from an _empty_ or temporary directory (which doesn't contain any Scala code), and then paste the following into the Sbt prompt:
```scala
set libraryDependencies += "com.github.darrenjw" %% "scala-smfsb" % "0.5"
set libraryDependencies += "org.scalanlp" %% "breeze-viz" % "0.13.2"
set scalaVersion := "2.12.6"
console
```
The first time you run this it will take a little while to download and cache various library dependcies. But everything is cached, so it should be much quicker in future. When it is finished, you should have a Scala REPL ready to enter Scala code.

## An introduction to `scala-smfsb`

It should be possible to type or copy-and-paste the commands below one-at-a-time into the Scala REPL. We need to start with a few imports.
```scala
import smfsb._
// import smfsb._

import breeze.linalg._
// import breeze.linalg._

import breeze.numerics._
// import breeze.numerics._
```
We are now ready to go. 

### Simulating models

Let's begin by instantiating a Lotka-Volterra model, simulating a single realisation of the process, and then plotting it.
```scala
// Simulate LV with Gillespie
val model = SpnModels.lv[IntState]()
// model: smfsb.Spn[smfsb.IntState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,smfsb.SpnModels$$$Lambda$1162/269175396@79affb77)

val step = Step.gillespie(model)
// step: (smfsb.IntState, smfsb.Time, smfsb.Time) => smfsb.IntState = smfsb.Step$$$Lambda$1173/939308285@563599a3

val ts = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, step)
// ts: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (0.05,DenseVector(53, 96)), (0.1,DenseVector(54, 96)), (0.15000000000000002,DenseVector(57, 94)), (0.2,DenseVector(59, 88)), (0.25,DenseVector(63, 86)), (0.3,DenseVector(63, 86)), (0.35,DenseVector(66, 88)), (0.39999999999999997,DenseVector(63, 88)), (0.44999999999999996,DenseVector(65, 86)), (0.49999999999999994,DenseVector(66, 88)), (0.5499999999999999,DenseVector(68, 86)), (0.6,DenseVector(71, 82)), (0.65,DenseVector(75, 81)), (0.7000000000000001,DenseVector(76, 82)), (0.7500000000000001,DenseVector(79, 82)), (0.8000000000000002,DenseVector(79, 84)), (0.8500000000000002,DenseVector(84, 83)), (0.9000000000000002,DenseVector(88, 80)), (0.9500000000000003,DenseVector(94, 77)), (1.0000000000000002,D...

Sim.plotTs(ts, "Gillespie simulation of LV model with default parameters")
```
When the model is instantiated, it can use default rate constants for the reactions. But these can be over-written. The library uses Breeze `DenseVectors` to represent parameter vectors. 
```scala
// Simulate LV with non-default parameters
val model2 = SpnModels.lv[IntState](DenseVector(1.0, 0.006, 0.3))
// model2: smfsb.Spn[smfsb.IntState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,smfsb.SpnModels$$$Lambda$1162/269175396@17ddd163)

val step2 = Step.gillespie(model2)
// step2: (smfsb.IntState, smfsb.Time, smfsb.Time) => smfsb.IntState = smfsb.Step$$$Lambda$1173/939308285@493d2921

val ts2 = Sim.ts(DenseVector(50, 40), 0.0, 50.0, 0.1, step2)
// ts2: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 40)), (0.1,DenseVector(54, 39)), (0.2,DenseVector(54, 42)), (0.30000000000000004,DenseVector(59, 42)), (0.4,DenseVector(65, 43)), (0.5,DenseVector(69, 42)), (0.6,DenseVector(77, 40)), (0.7,DenseVector(80, 41)), (0.7999999999999999,DenseVector(82, 42)), (0.8999999999999999,DenseVector(89, 43)), (0.9999999999999999,DenseVector(96, 40)), (1.0999999999999999,DenseVector(100, 43)), (1.2,DenseVector(106, 45)), (1.3,DenseVector(112, 46)), (1.4000000000000001,DenseVector(117, 51)), (1.5000000000000002,DenseVector(119, 54)), (1.6000000000000003,DenseVector(124, 61)), (1.7000000000000004,DenseVector(130, 62)), (1.8000000000000005,DenseVector(138, 66)), (1.9000000000000006,DenseVector(152, 66)), (2.0000000000000004...

Sim.plotTs(ts2, "Gillespie simulation of LV model with non-default parameters")
```
The library comes with a few other models. There's a Michaelis-Menten enzyme kinetics model:
```scala
// Simulate other models with Gillespie
val stepMM = Step.gillespie(SpnModels.mm[IntState]())
// stepMM: (smfsb.IntState, smfsb.Time, smfsb.Time) => smfsb.IntState = smfsb.Step$$$Lambda$1173/939308285@6a6c4042

val tsMM = Sim.ts(DenseVector(301,120,0,0), 0.0, 100.0, 0.5, stepMM)
// tsMM: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(301, 120, 0, 0)), (0.5,DenseVector(280, 100, 20, 1)), (1.0,DenseVector(267, 88, 32, 2)), (1.5,DenseVector(251, 73, 47, 3)), (2.0,DenseVector(239, 63, 57, 5)), (2.5,DenseVector(230, 61, 59, 12)), (3.0,DenseVector(215, 48, 72, 14)), (3.5,DenseVector(208, 43, 77, 16)), (4.0,DenseVector(201, 44, 76, 24)), (4.5,DenseVector(192, 39, 81, 28)), (5.0,DenseVector(183, 33, 87, 31)), (5.5,DenseVector(181, 33, 87, 33)), (6.0,DenseVector(176, 31, 89, 36)), (6.5,DenseVector(172, 33, 87, 42)), (7.0,DenseVector(170, 41, 79, 52)), (7.5,DenseVector(165, 41, 79, 57)), (8.0,DenseVector(159, 37, 83, 59)), (8.5,DenseVector(155, 38, 82, 64)), (9.0,DenseVector(151, 35, 85, 65)), (9.5,DenseVector(145, 29, 91, 65)), (10.0,DenseVector...

Sim.plotTs(tsMM, "Gillespie simulation of the MM model")
```
and an immigration-death model
```scala
val stepID = Step.gillespie(SpnModels.id[IntState]())
// stepID: (smfsb.IntState, smfsb.Time, smfsb.Time) => smfsb.IntState = smfsb.Step$$$Lambda$1173/939308285@4d44a24c

val tsID = Sim.ts(DenseVector(0), 0.0, 40.0, 0.1, stepID)
// tsID: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(0)), (0.1,DenseVector(0)), (0.2,DenseVector(0)), (0.30000000000000004,DenseVector(0)), (0.4,DenseVector(0)), (0.5,DenseVector(0)), (0.6,DenseVector(0)), (0.7,DenseVector(0)), (0.7999999999999999,DenseVector(0)), (0.8999999999999999,DenseVector(1)), (0.9999999999999999,DenseVector(1)), (1.0999999999999999,DenseVector(1)), (1.2,DenseVector(1)), (1.3,DenseVector(1)), (1.4000000000000001,DenseVector(1)), (1.5000000000000002,DenseVector(1)), (1.6000000000000003,DenseVector(1)), (1.7000000000000004,DenseVector(1)), (1.8000000000000005,DenseVector(1)), (1.9000000000000006,DenseVector(1)), (2.0000000000000004,DenseVector(1)), (2.1000000000000005,DenseVector(1)), (2.2000000000000006,DenseVector(1)), (2.3000000000000...

Sim.plotTs(tsID, "Gillespie simulation of the ID model")
```
and an auto-regulatory genetic network model.
```scala
val stepAR = Step.gillespie(SpnModels.ar[IntState]())
// stepAR: (smfsb.IntState, smfsb.Time, smfsb.Time) => smfsb.IntState = smfsb.Step$$$Lambda$1173/939308285@2891b8d4

val tsAR = Sim.ts(DenseVector(10, 0, 0, 0, 0), 0.0, 500.0, 0.5, stepAR)
// tsAR: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(10, 0, 0, 0, 0)), (0.5,DenseVector(4, 6, 0, 4, 4)), (1.0,DenseVector(4, 6, 0, 2, 5)), (1.5,DenseVector(3, 7, 1, 6, 6)), (2.0,DenseVector(4, 6, 1, 3, 9)), (2.5,DenseVector(5, 5, 1, 4, 9)), (3.0,DenseVector(6, 4, 1, 8, 10)), (3.5,DenseVector(8, 2, 1, 2, 15)), (4.0,DenseVector(3, 7, 1, 7, 19)), (4.5,DenseVector(6, 4, 1, 5, 19)), (5.0,DenseVector(5, 5, 1, 8, 22)), (5.5,DenseVector(6, 4, 1, 7, 22)), (6.0,DenseVector(8, 2, 1, 3, 25)), (6.5,DenseVector(7, 3, 1, 8, 24)), (7.0,DenseVector(9, 1, 1, 10, 23)), (7.5,DenseVector(7, 3, 0, 12, 25)), (8.0,DenseVector(8, 2, 0, 5, 27)), (8.5,DenseVector(7, 3, 0, 5, 28)), (9.0,DenseVector(8, 2, 0, 11, 24)), (9.5,DenseVector(7, 3, 0, 7, 27)), (10.0,DenseVector(7, 3, 0, 7, 27)),...

Sim.plotTs(tsAR, "Gillespie simulation of the AR model")
```
If you know the book and/or the R package, these models should all be familiar. We don't have to simulate data on a regular time grid.
```scala
// Simulate on an irregular time grid
val tsi = Sim.times(DenseVector(50,100), 0.0, List(0.0,2.0,5.0,10.0,20.0), step)
// tsi: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (2.0,DenseVector(152, 75)), (5.0,DenseVector(123, 424)), (10.0,DenseVector(94, 77)), (20.0,DenseVector(177, 42)))

Sim.plotTs(tsi, "Simulation on an irregular time grid")
```
We also don't have to just sample one realisation. We can look at many realisations of the same transition kernel, and then use Breeze-viz to plot the results.
```scala
// Simulate a sample
val samp = Sim.sample(1000, DenseVector(50,100), 0.0, 10.0, step)
// samp: List[smfsb.IntState] = List(DenseVector(108, 53), DenseVector(81, 75), DenseVector(85, 58), DenseVector(145, 49), DenseVector(66, 84), DenseVector(75, 93), DenseVector(109, 115), DenseVector(74, 115), DenseVector(33, 46), DenseVector(88, 68), DenseVector(117, 64), DenseVector(10, 34), DenseVector(135, 89), DenseVector(112, 189), DenseVector(39, 90), DenseVector(51, 58), DenseVector(83, 53), DenseVector(102, 54), DenseVector(125, 126), DenseVector(131, 89), DenseVector(65, 67), DenseVector(51, 55), DenseVector(16, 79), DenseVector(89, 123), DenseVector(10, 41), DenseVector(38, 91), DenseVector(133, 86), DenseVector(81, 43), DenseVector(67, 60), DenseVector(173, 132), DenseVector(117, 50), DenseVector(60, 47), DenseVector(57, 52), DenseVector(185, 106), Den...

import breeze.plot._
// import breeze.plot._

val fig = Figure("Marginal of transition kernel")
// fig: breeze.plot.Figure = breeze.plot.Figure@1fc34fac

fig.subplot(1,2,0) += hist(DenseVector(samp.map(_.data(0)).toArray))
// res11: breeze.plot.Plot = breeze.plot.Plot@5b811492

fig.subplot(1,2,1) += hist(DenseVector(samp.map(_.data(1)).toArray))
// res12: breeze.plot.Plot = breeze.plot.Plot@70c2c864
```
We are not restricted to exact stochastic simulation using the Gillespie algorithm. We can use an approximate Poisson time-stepping algorithm.
```scala
// Simulate LV with other algorithms
val stepPts = Step.pts(model)
// stepPts: (smfsb.IntState, smfsb.Time, smfsb.Time) => smfsb.IntState = smfsb.Step$$$Lambda$1434/1991353440@e3b7d93

val tsPts = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, stepPts)
// tsPts: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (0.05,DenseVector(54, 95)), (0.1,DenseVector(55, 96)), (0.15000000000000002,DenseVector(55, 98)), (0.2,DenseVector(56, 98)), (0.25,DenseVector(55, 97)), (0.3,DenseVector(59, 96)), (0.35,DenseVector(64, 92)), (0.39999999999999997,DenseVector(71, 92)), (0.44999999999999996,DenseVector(73, 88)), (0.49999999999999994,DenseVector(76, 88)), (0.5499999999999999,DenseVector(75, 88)), (0.6,DenseVector(75, 87)), (0.65,DenseVector(72, 84)), (0.7000000000000001,DenseVector(75, 83)), (0.7500000000000001,DenseVector(76, 85)), (0.8000000000000002,DenseVector(76, 87)), (0.8500000000000002,DenseVector(82, 82)), (0.9000000000000002,DenseVector(88, 81)), (0.9500000000000003,DenseVector(88, 80)), (1.000000000000000...

Sim.plotTs(tsPts, "Poisson time-step simulation of the LV model")
```
Alternatively, we can instantiate the example models using a continuous state rather than a discrete state, and then simulate using algorithms based on continous approximations, such as Euler-Maruyama simulation of a chemical Langevin equation (CLE) approximation. 
```scala
val stepCle = Step.cle(SpnModels.lv[DoubleState]())
// stepCle: (smfsb.DoubleState, smfsb.Time, smfsb.Time) => smfsb.DoubleState = smfsb.Step$$$Lambda$1444/26680819@399538b1

val tsCle = Sim.ts(DenseVector(50.0, 100.0), 0.0, 20.0, 0.05, stepCle)
// tsCle: smfsb.Ts[smfsb.DoubleState] = List((0.0,DenseVector(50.0, 100.0)), (0.05,DenseVector(52.95718788382351, 96.26383094244784)), (0.1,DenseVector(54.9187870537633, 93.38910347809582)), (0.15000000000000002,DenseVector(59.07291677971403, 90.72532379721382)), (0.2,DenseVector(61.661590536946754, 89.0219636960876)), (0.25,DenseVector(65.12787505105064, 86.1300012509824)), (0.3,DenseVector(65.8705837614942, 85.22079617713672)), (0.35,DenseVector(69.92189575142864, 82.97609532704504)), (0.39999999999999997,DenseVector(70.72473396511216, 84.17118903363327)), (0.44999999999999996,DenseVector(72.30816107505782, 85.60523679770631)), (0.49999999999999994,DenseVector(77.0858710833851, 86.04986368143605)), (0.5499999999999999,DenseVector(78.55414288441473, 83.2706216930...

Sim.plotTs(tsCle, "Euler-Maruyama/CLE simulation of the LV model")
```
If we want to ignore noise temporarily, there's also a simple continuous deterministic Euler integrator built-in.
```scala
val stepE = Step.euler(SpnModels.lv[DoubleState]())
// stepE: (smfsb.DoubleState, smfsb.Time, smfsb.Time) => smfsb.DoubleState = smfsb.Step$$$Lambda$1448/1906277420@2c2bd43d

val tsE = Sim.ts(DenseVector(50.0, 100.0), 0.0, 20.0, 0.05, stepE)
// tsE: smfsb.Ts[smfsb.DoubleState] = List((0.0,DenseVector(50.0, 100.0)), (0.05,DenseVector(51.27142658603865, 98.27463897907421)), (0.1,DenseVector(52.59743114538847, 96.61038171208362)), (0.15000000000000002,DenseVector(53.979743954818325, 95.00643765836239)), (0.2,DenseVector(55.42015867657027, 93.46206382414753)), (0.25,DenseVector(56.92053213395618, 91.9765667028639)), (0.3,DenseVector(58.48278392434014, 90.54930430443086)), (0.35,DenseVector(60.10889583970137, 89.17968829179253)), (0.39999999999999997,DenseVector(61.80091106213609, 87.86718624360923)), (0.44999999999999996,DenseVector(63.560933098492534, 86.61132406293969)), (0.49999999999999994,DenseVector(65.39112441481272, 85.41168855279767)), (0.5499999999999999,DenseVector(67.29370472734618, 84.2679301...

Sim.plotTs(tsE, "Continuous-deterministic Euler simulation of the LV model")
```

### Defining models

There are a few built-in models, as seen above. But quite quickly you are likely to want to define your own. This is also very straightforward, using the constructor of the `UnmarkedSpn` class. Let's just pretend that the Lotka-Volterra model is not included in the library, and think about how we could define it from scratch. The simplest approach would be something like the following.
```scala
val mylv0 = UnmarkedSpn[IntState](
  List("x", "y"),
  DenseMatrix((1, 0), (1, 1), (0, 1)),
  DenseMatrix((2, 0), (0, 2), (0, 0)),
  (x, t) => {
    DenseVector(
      x.data(0) * 1.0, x.data(0) * x.data(1) * 0.005, x.data(1) * 0.6
    )}
)
// mylv0: smfsb.UnmarkedSpn[smfsb.IntState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,$$Lambda$1473/1404744034@7d3d9b6e)
```
We create a fully parametrised model (without an initial marking), but providing a list of species names, a *Pre* and *Post* matrix, and a hazard vector, which in general may be a function of both the state, `x` and the current time, `t`. Note that it should be OK to write, say, `x(0)`, rather than `x.data(0)`, but sometimes correct resolution of the indexing fails with `IntState` (but doesn't for `DoubleState`). We can test that this works.
```scala
val ts0 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(mylv0))
// ts0: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (0.05,DenseVector(50, 93)), (0.1,DenseVector(51, 90)), (0.15000000000000002,DenseVector(53, 80)), (0.2,DenseVector(51, 79)), (0.25,DenseVector(54, 77)), (0.3,DenseVector(57, 75)), (0.35,DenseVector(62, 76)), (0.39999999999999997,DenseVector(63, 75)), (0.44999999999999996,DenseVector(66, 73)), (0.49999999999999994,DenseVector(71, 73)), (0.5499999999999999,DenseVector(72, 70)), (0.6,DenseVector(71, 72)), (0.65,DenseVector(71, 73)), (0.7000000000000001,DenseVector(71, 74)), (0.7500000000000001,DenseVector(69, 75)), (0.8000000000000002,DenseVector(69, 74)), (0.8500000000000002,DenseVector(69, 74)), (0.9000000000000002,DenseVector(71, 71)), (0.9500000000000003,DenseVector(72, 70)), (1.0000000000000002,...

Sim.plotTs(ts0, "Gillespie simulation of LV0")
```
One potential issue with this definition is that the rate constants within the hazard vector are hard-coded. We can easily get around that by creating a function (or method) that accepts a parameter vector (of some kind) and outputs a fully parameterised SPN.
```scala
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
// lvparam: (p: breeze.linalg.DenseVector[Double])smfsb.Spn[smfsb.IntState]
```
Using a method allows the inclusion of a default parameter vector, which can be convenient. The follow code shows how we can use this.
```scala
val mylv1 = lvparam(DenseVector(1.0, 0.005, 0.6))
// mylv1: smfsb.Spn[smfsb.IntState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,$$Lambda$1497/1454059650@153ed59b)

val tslv1 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(mylv1))
// tslv1: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (0.05,DenseVector(50, 97)), (0.1,DenseVector(52, 94)), (0.15000000000000002,DenseVector(52, 90)), (0.2,DenseVector(52, 89)), (0.25,DenseVector(56, 86)), (0.3,DenseVector(55, 88)), (0.35,DenseVector(55, 86)), (0.39999999999999997,DenseVector(54, 86)), (0.44999999999999996,DenseVector(56, 84)), (0.49999999999999994,DenseVector(56, 82)), (0.5499999999999999,DenseVector(54, 81)), (0.6,DenseVector(59, 78)), (0.65,DenseVector(60, 77)), (0.7000000000000001,DenseVector(59, 77)), (0.7500000000000001,DenseVector(59, 74)), (0.8000000000000002,DenseVector(60, 75)), (0.8500000000000002,DenseVector(62, 73)), (0.9000000000000002,DenseVector(65, 69)), (0.9500000000000003,DenseVector(67, 70)), (1.000000000000000...

Sim.plotTs(tslv1, "Gillespie simulation of LV1")

val mylv2 = lvparam(DenseVector(1.1, 0.01, 0.6))
// mylv2: smfsb.Spn[smfsb.IntState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,$$Lambda$1497/1454059650@3554e644)

val tslv2 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(mylv2))
// tslv2: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (0.05,DenseVector(50, 100)), (0.1,DenseVector(51, 98)), (0.15000000000000002,DenseVector(51, 99)), (0.2,DenseVector(53, 97)), (0.25,DenseVector(53, 99)), (0.3,DenseVector(50, 102)), (0.35,DenseVector(50, 96)), (0.39999999999999997,DenseVector(48, 96)), (0.44999999999999996,DenseVector(50, 93)), (0.49999999999999994,DenseVector(52, 94)), (0.5499999999999999,DenseVector(53, 93)), (0.6,DenseVector(55, 89)), (0.65,DenseVector(58, 89)), (0.7000000000000001,DenseVector(56, 91)), (0.7500000000000001,DenseVector(57, 90)), (0.8000000000000002,DenseVector(58, 89)), (0.8500000000000002,DenseVector(60, 87)), (0.9000000000000002,DenseVector(62, 84)), (0.9500000000000003,DenseVector(62, 85)), (1.0000000000000...

Sim.plotTs(tslv2, "Gillespie simulation of LV2")

val mylv3: Spn[IntState] = lvparam()
// mylv3: smfsb.Spn[smfsb.IntState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,$$Lambda$1497/1454059650@6c619118)

val tslv3 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(mylv3))
// tslv3: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (0.05,DenseVector(53, 99)), (0.1,DenseVector(53, 97)), (0.15000000000000002,DenseVector(55, 94)), (0.2,DenseVector(57, 88)), (0.25,DenseVector(56, 88)), (0.3,DenseVector(58, 90)), (0.35,DenseVector(60, 89)), (0.39999999999999997,DenseVector(60, 89)), (0.44999999999999996,DenseVector(59, 88)), (0.49999999999999994,DenseVector(57, 88)), (0.5499999999999999,DenseVector(57, 87)), (0.6,DenseVector(59, 84)), (0.65,DenseVector(59, 83)), (0.7000000000000001,DenseVector(59, 80)), (0.7500000000000001,DenseVector(60, 80)), (0.8000000000000002,DenseVector(61, 79)), (0.8500000000000002,DenseVector(59, 80)), (0.9000000000000002,DenseVector(66, 78)), (0.9500000000000003,DenseVector(63, 81)), (1.000000000000000...

Sim.plotTs(tslv3, "Gillespie simulation of LV3")
```
So, this is how we can define a SPN with a discrete state, intended for discrete stochastic simulation. By instead parameterising with a `DoubleState`, we can create models intended for continuous simulation, for example, using `Step.cle`. However, very often we want to use the same model for both discrete and continuous stochastic simulation. We can do that too, by making our creation function allow any state belonging to the `State` type class. Again, for the Lotka-Volterra model, the definition of the built-in model is:
```scala
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
// lv: [S](p: breeze.linalg.DenseVector[Double])(implicit evidence$1: smfsb.State[S])smfsb.Spn[S]
```
Note the use of `.toDvd` to convert a state to a `DenseVector[Double]`, which is necessary since we do not require that all instances of the `State` type class are explicitly indexed. We can use this as we have already seen, by specifying the particular `State` to use for instantiation at call time.
```scala
val lvDiscrete = lv[IntState]()
// lvDiscrete: smfsb.Spn[smfsb.IntState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,$$Lambda$1515/1507796176@329a7708)

val tsDiscrete = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(lvDiscrete))
// tsDiscrete: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (0.05,DenseVector(51, 102)), (0.1,DenseVector(54, 99)), (0.15000000000000002,DenseVector(50, 102)), (0.2,DenseVector(49, 101)), (0.25,DenseVector(53, 98)), (0.3,DenseVector(52, 99)), (0.35,DenseVector(50, 101)), (0.39999999999999997,DenseVector(51, 95)), (0.44999999999999996,DenseVector(51, 96)), (0.49999999999999994,DenseVector(53, 94)), (0.5499999999999999,DenseVector(55, 90)), (0.6,DenseVector(55, 90)), (0.65,DenseVector(58, 86)), (0.7000000000000001,DenseVector(62, 85)), (0.7500000000000001,DenseVector(62, 81)), (0.8000000000000002,DenseVector(65, 81)), (0.8500000000000002,DenseVector(65, 81)), (0.9000000000000002,DenseVector(69, 81)), (0.9500000000000003,DenseVector(71, 81)), (1.000000...

Sim.plotTs(tsDiscrete, "Gillespie simulation of lvDiscrete")

val lvDiscrete2 = lv[IntState](DenseVector(1.1, 0.01, 0.6))
// lvDiscrete2: smfsb.Spn[smfsb.IntState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,$$Lambda$1515/1507796176@22fcc4a3)

val tsDiscrete2 = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, Step.gillespie(lvDiscrete2))
// tsDiscrete2: smfsb.Ts[smfsb.IntState] = List((0.0,DenseVector(50, 100)), (0.05,DenseVector(50, 102)), (0.1,DenseVector(54, 98)), (0.15000000000000002,DenseVector(54, 97)), (0.2,DenseVector(54, 94)), (0.25,DenseVector(55, 91)), (0.3,DenseVector(57, 89)), (0.35,DenseVector(55, 90)), (0.39999999999999997,DenseVector(56, 86)), (0.44999999999999996,DenseVector(62, 83)), (0.49999999999999994,DenseVector(62, 84)), (0.5499999999999999,DenseVector(63, 82)), (0.6,DenseVector(62, 81)), (0.65,DenseVector(60, 80)), (0.7000000000000001,DenseVector(59, 80)), (0.7500000000000001,DenseVector(62, 77)), (0.8000000000000002,DenseVector(64, 75)), (0.8500000000000002,DenseVector(65, 78)), (0.9000000000000002,DenseVector(63, 79)), (0.9500000000000003,DenseVector(63, 83)), (1.00000000...

Sim.plotTs(tsDiscrete2, "Gillespie simulation of lvDiscrete2")

val lvCts = lv[DoubleState]()
// lvCts: smfsb.Spn[smfsb.DoubleState] =
// UnmarkedSpn(List(x, y),1  0
// 1  1
// 0  1  ,2  0
// 0  2
// 0  0  ,$$Lambda$1515/1507796176@28c96689)

val tsCts = Sim.ts(DenseVector(50.0, 100.0), 0.0, 20.0, 0.05, Step.cle(lvCts))
// tsCts: smfsb.Ts[smfsb.DoubleState] = List((0.0,DenseVector(50.0, 100.0)), (0.05,DenseVector(55.91260101122111, 95.6664865870923)), (0.1,DenseVector(59.88356270529999, 93.63876518463259)), (0.15000000000000002,DenseVector(62.952644326307016, 93.42943897222844)), (0.2,DenseVector(63.21134535455165, 92.70576334685782)), (0.25,DenseVector(64.95867823474966, 93.59639460232206)), (0.3,DenseVector(66.22818196271625, 90.54188003012806)), (0.35,DenseVector(69.09338078582286, 87.87589314119442)), (0.39999999999999997,DenseVector(72.04609489321803, 84.5615769486736)), (0.44999999999999996,DenseVector(76.0223160823402, 83.51003733336009)), (0.49999999999999994,DenseVector(80.00245043821688, 81.55946755588991)), (0.5499999999999999,DenseVector(80.23422300759088, 80.43004582...

Sim.plotTs(tsCts, "Gillespie simulation of lvCts")
```
This approach allows us to define models that can be used for both discrete and continuous stochastic (and deterministic) simulations, and is therefore the recommended approach in cases where both discrete and continuous simulation makes sense.

## Next steps

Having worked through the tutorial, next it would make sense to first run and then study the code of the [examples](../examples/). In particular, the examples cover both spatial reaction-diffusion simulation and the problem of how to do parameter inference from data. After that, the [API documentation](https://darrenjw.github.io/scala-smfsb/api/smfsb/index.html) should make some sense. After that, studying the [source code](../src/main/scala/smfsb/) will be helpful. Looking at the [test code](../src/test/scala/) can also be useful. For more on Scala, and especially its use for scientific and statistical computing, take a look at my [Scala course](https://github.com/darrenjw/scala-course/blob/master/StartHere.md) and my [blog](https://darrenjw.wordpress.com/).


#### eof

