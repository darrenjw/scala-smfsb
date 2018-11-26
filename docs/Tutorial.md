# scala-smfsb tutorial

This tutorial document will walk through some basic features of the `scala-smfsb` library. Some familiarity with both Scala and the `smfsb` R package will be helpful, but is not strictly necessary. Note that the library relies on the Scala [Breeze](https://github.com/scalanlp/breeze/blob/master/README.md) library for linear algebra and probability distributions, so some familiarity with that library can also be helpful.

## Setup

To follow the tutorial, you need to have [Sbt](http://www.scala-sbt.org/) installed, and this in turn requires a recent [JDK](http://www.oracle.com/technetwork/java/javase/downloads). If you are new to Scala, you may find the [setup page](https://github.com/darrenjw/scala-course/blob/master/Setup.md) for my [Scala course](https://github.com/darrenjw/scala-course/blob/master/StartHere.md) to be useful, but note that on many Linux systems it can be as simple as installing the packages `openjdk-8-jdk` and `sbt`.

Once you have Sbt installed, you should be able to run it by entering `sbt` at your OS command line. You now need to use Sbt to create a Scala REPL with a dependency on the `scala-smfsb` library. There are many ways to do this, but if you are new to Scala, the simplest way is probably to start up Sbt from an _empty_ or temporary directory (which doesn't contain any Scala code), and then paste the following into the Sbt prompt:
```scala
set libraryDependencies += "com.github.darrenjw" %% "scala-smfsb" % "0.2"
set libraryDependencies += "org.scalanlp" %% "breeze-viz" % "0.13.2"
set scalaVersion := "2.12.6"
console
```
The first time you run this it will take a little while to download and cache various library dependcies. But everything is cached, so it should be much quicker in future. When it is finished, you should have a Scala REPL ready to enter Scala code.

## An introduction to `scala-smfsb`

It should be possible to type or copy-and-paste the commands below one-at-a-time into the Scala REPL. We need to start with a few imports.
```scala
import smfsb._
import breeze.linalg._
import breeze.numerics._
```
We are now ready to go. Let's begin by instantiating a Lotka-Volterra model, simulating a single realisation of the process, and then plotting it.
```scala
// Simulate LV with Gillespie
val model = SpnModels.lv[IntState]()
val step = Step.gillespie(model)
val ts = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, step)
Sim.plotTs(ts, "Gillespie simulation of LV model with default parameters")
```
When the model is instantiated, it can use default rate constants for the reactions. But these can be over-written. The library uses Breeze `DenseVectors` to represent parameter vectors. 
```scala
// Simulate LV with non-default parameters
val model2 = SpnModels.lv[IntState](DenseVector(1.0, 0.006, 0.3))
val step2 = Step.gillespie(model2)
val ts2 = Sim.ts(DenseVector(50, 40), 0.0, 50.0, 0.1, step2)
Sim.plotTs(ts2, "Gillespie simulation of LV model with non-default parameters")
```
The library comes with a few other models. There's a Michaelis-Menten enzyme kinetics model:
```scala
// Simulate other models with Gillespie
val stepMM = Step.gillespie(SpnModels.mm[IntState]())
val tsMM = Sim.ts(DenseVector(301,120,0,0), 0.0, 100.0, 0.5, stepMM)
Sim.plotTs(tsMM, "Gillespie simulation of the MM model")
```
and an immigration-death model
```scala
val stepID = Step.gillespie(SpnModels.id[IntState]())
val tsID = Sim.ts(DenseVector(0), 0.0, 40.0, 0.1, stepID)
Sim.plotTs(tsID, "Gillespie simulation of the ID model")
```
and an auto-regulatory genetic network model.
```scala
val stepAR = Step.gillespie(SpnModels.ar[IntState]())
val tsAR = Sim.ts(DenseVector(10, 0, 0, 0, 0), 0.0, 500.0, 0.5, stepAR)
Sim.plotTs(tsAR, "Gillespie simulation of the ID model")
```
If you know the book and/or the R package, these models should all be familiar. We don't have to simulate data on a regular time grid.
```scala
// Simulate on an irregular time grid
val tsi = Sim.times(DenseVector(50,100), 0.0, List(0.0,2.0,5.0,10.0,20.0), step)
Sim.plotTs(tsi, "Simulation on an irregular time grid")
```
We also don't have to just sample one realisation. We can look at many realisations of the same transition kernel, and then use Breeze-viz to plot the results.
```scala
// Simulate a sample
val samp = Sim.sample(1000, DenseVector(50,100), 0.0, 10.0, step)
import breeze.plot._
val fig = Figure("Marginal of transition kernel")
fig.subplot(1,2,0) += hist(DenseVector(samp.map(_.data(0)).toArray))
fig.subplot(1,2,1) += hist(DenseVector(samp.map(_.data(1)).toArray))
```
We are not restricted to exact stochastic simulation using the Gillespie algorithm. We can use an approximate Poisson time-stepping algorithm.
```scala
// Simulate LV with other algorithms
val stepPts = Step.pts(model)
val tsPts = Sim.ts(DenseVector(50, 100), 0.0, 20.0, 0.05, stepPts)
Sim.plotTs(tsPts, "Poisson time-step simulation of the LV model")
```
Alternatively, we can instantiate the example models using a continuous state rather than a discrete state, and then simulate using algorithms based on continous approximations, such as Euler-Maruyama simulation of a chemical Langevin equation (CLE) approximation. 
```scala
val stepCle = Step.cle(SpnModels.lv[DoubleState]())
val tsCle = Sim.ts(DenseVector(50.0, 100.0), 0.0, 20.0, 0.05, stepCle)
Sim.plotTs(tsCle, "Euler-Maruyama/CLE simulation of the LV model")
```
If we want to ignore noise temporarily, there's also a simple continous deterministic Euler integrator built-in.
```scala
val stepE = Step.euler(SpnModels.lv[DoubleState]())
val tsE = Sim.ts(DenseVector(50.0, 100.0), 0.0, 20.0, 0.05, stepE)
Sim.plotTs(tsE, "Continuous-deterministic Euler simulation of the LV model")
```

## Next steps

Having worked through the tutorial, next it would make sense to first run and then study the code of the [examples](../examples/). In particular, the examples cover the problem of how to do parameter inference from data. After that, the [API documentation](https://darrenjw.github.io/scala-smfsb/api/smfsb/index.html) should make some sense. After that, studying the [source code](../src/main/scala/smfsb/) will be helpful. Looking at the [test code](../src/test/scala/) can also be useful. For more on Scala, and especially its use for scientific and statistical computing, take a look at my [Scala course](https://github.com/darrenjw/scala-course/blob/master/StartHere.md) and my [blog](https://darrenjw.wordpress.com/).


#### eof

