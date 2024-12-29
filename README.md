# scala-smfsb

[![Tests](https://github.com/darrenjw/scala-smfsb/actions/workflows/ci.yml/badge.svg)](https://github.com/darrenjw/scala-smfsb/actions)
[![Pages](https://github.com/darrenjw/scala-smfsb/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/darrenjw/scala-smfsb/actions/workflows/pages/pages-build-deployment)

### Scala library for stochastic kinetic modelling, biochemical network simulation, and parameter inference, associated with the 3rd edition of the textbook Stochastic Modelling for Systems Biology

* The current *stable* version of this library is "1.1", for Scala 2.13 and **Scala 3**
* Use version "0.7" for Scala 2.11 and 2.12
* The current unstable *snapshot* release of this library is "1.2-SNAPSHOT" (cross-built for Scala 2.13 and Scala 3)

Binaries are published to Sonatype.

## Quickstart

This library is published to the Sonatype Central Repository (formally known as Maven Central), and hence can be used with any standard Scala build tool. The recommended ways to use it are via either [sbt](http://www.scala-sbt.org/) or [scala-cli](https://scala-cli.virtuslab.org/), and the recommended way to install and set up your Scala environment is using [coursier](https://get-coursier.io/docs/cli-installation). Coursier setup (`cs setup`) will install both `sbt` and `scala-cli` by default.

A minimal example for `scala-cli` is given below:
```scala
//> using scala 3.3.4
//> using dep com.github.darrenjw::scala-smfsb:1.1
//> using dep org.scalanlp::breeze-viz:2.1.1

import smfsb.*
import breeze.linalg.*

@main
def run() =
  val model = SpnModels.lv[IntState]()
  val step = Step.gillespie(model)
  val ts = Sim.ts(DenseVector(50, 40), 0.0, 20.0, 0.1, step)
  Sim.plotTs(ts)

```
* Copy this into a file called (say) `lv.scala`, and then run from the command line with `scala-cli lv.scala`.
* This should simulate a trajectory from a Lotka-Volterra model and plot the result in a window on the console which looks a little bit like the following:

![Lotka-Volterra trajectory](LV-trajectory.png)

To get the most out of this library, it will be helpful if you are already familiar with the R package [smfsb](https://cran.r-project.org/package=smfsb) associated with the (2nd or) 3rd edition of the textbook [Stochastic modelling for systems biology](https://github.com/darrenjw/smfsb/), and have a basic familiarity with [Scala](https://www.scala-lang.org/) and [Breeze](https://github.com/scalanlp/breeze). For those new to Scala, my course on [Scala for statistical computing](https://github.com/darrenjw/scala-course/blob/master/SelfStudyGuide.md) is a good place to start.

## Documentation

* [Tutorial](docs/Tutorial.md) - walk through of basic functionality
* There are more interesting examples in the [examples directory](examples/). To run them, download or clone the repo, and do `sbt run` from *inside* the examples directory.
* [Latest API docs](https://darrenjw.github.io/scala-smfsb/smfsb.html)

## Using the library in Scala `sbt` projects

### giter8 template:

To create a new Scala `sbt` project template, just use:
```bash
sbt new darrenjw/scala-smfsb.g8
```
This will create a new project including a dependence on the stable version of the library.

### Stable:

Just add:
```scala
"com.github.darrenjw" %% "scala-smfsb" % "1.1"
```
to your `sbt` library dependencies. You might also need to add an explicit dependence on `breeze-viz`:
```scala
"org.scalanlp" %% "breeze-viz" % "2.1.0"
```

### Snapshot:

Just add something like:
```scala
libraryDependencies += "com.github.darrenjw" %% "scala-smfsb" % "1.2-SNAPSHOT"
resolvers += "Sonatype Snapshots" at
    "https://oss.sonatype.org/content/repositories/snapshots/"
```
to your `sbt` build file.

## Building from source

Download or clone the repo and do something like:
```scala
sbt clean compile doc mdoc test +package
```
from the top-level directory (the directory containing `build.sbt`).

