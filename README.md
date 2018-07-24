# scala-smfsb

### Scala library for biochemical network simulation, associated with the 3rd edition of the textbook Stochastic Modelling for Systems Biology

* The current *stable* version of this library is COMING SOON!
* The current unstable *snapshot* release of this library is "0.1-SNAPSHOT"

Binaries are published to Sonatype.

## Quickstart

* To use this software library, you should first install a recent [JDK]() and [Sbt]().
* Run `sbt` from an empty/temp directory
* At the `sbt` prompt, enter:
```scala
set libraryDependencies += "com.github.darrenjw" %% "scala-smfsb" % "0.1-SNAPSHOT"
set libraryDependencies += "org.scalanlp" %% "breeze-viz" % "0.13"
set scalaVersion := "2.12.6"
console
```
* You should now have a Scala REPL with a dependency on this library. At the Scala REPL, enter the following:
```scala
import smfsb.SpnExamples._
import smfsb.Sim._
import breeze.linalg._

val step = stepLv(lvparam)
val ts = simTs(DenseVector(50, 40), 0.0, 20.0, 0.1, step)
plotTs(ts)
```
* This should simulate a trajectory from a Lotka-Volterra model and plot the result in a window on the console which looks a little bit like the following:

![Lotka-Volterra trajectory](LV-trajectory.png)

To get the most out of this library, you should already be familiar with the R package [smfsb]() associated with the (2nd or) 3rd edition of the textbook [Stochastic modelling for systems biology](../smfsb/), and have a basic familiarity with [Scala]() and [Breeze](). For those new to Scala, my course on [Scala for statistical computing](../scala-course/) is a good place to start.

## Examples

There are more interesting examples in the [examples directory](examples/). To run them, download or clone the repo, and do `sbt run` from *inside* the examples directory.

## Using the library in your own Scala projects

Just add:
```scala
"com.github.darrenjw" %% "scala-smfsb" % "0.1-SNAPSHOT"
```
to your Sbt dependencies.

## Building from source

Download or clone the repo and do:
```scala
sbt clean compile test doc package
```
from the top-level directory.

