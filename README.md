# scala-smfsb

### Scala library for stochastic kinetic modelling, biochemical network simulation, and parameter inference, associated with the 3rd edition of the textbook Stochastic Modelling for Systems Biology

* The current *stable* version of this library is "0.9", for Scala 2.13 and **Scala 3**
* Use version "0.7" for Scala 2.11 and 2.12
* The current unstable *snapshot* release of this library is "1.0-SNAPSHOT" (cross-built for Scala 2.13 and Scala 3)

Binaries are published to Sonatype.

## Quickstart

* To use this software library, you should first install a recent [JDK](http://www.oracle.com/technetwork/java/javase/downloads) and [sbt](http://www.scala-sbt.org/).
* Run `sbt` from an empty/temp directory:
```bash
sbt "-Dsbt.version=1.5.1"
set libraryDependencies += "com.github.darrenjw" %% "scala-smfsb" % "0.9"
set libraryDependencies += "org.scalanlp" %% "breeze-viz" % "2.0"
set scalaVersion := "3.0.1"
console
```
* You should now have a Scala REPL with a dependency on this library. At the Scala REPL, enter the following:
```scala
import smfsb.*
import breeze.linalg.*
val model = SpnModels.lv[IntState]()
val step = Step.gillespie(model)
val ts = Sim.ts(DenseVector(50, 40), 0.0, 20.0, 0.1, step)
Sim.plotTs(ts)
```
* This should simulate a trajectory from a Lotka-Volterra model and plot the result in a window on the console which looks a little bit like the following:

![Lotka-Volterra trajectory](LV-trajectory.png)

To get the most out of this library, it will be helpful if you are already familiar with the R package [smfsb](https://cran.r-project.org/package=smfsb) associated with the (2nd or) 3rd edition of the textbook [Stochastic modelling for systems biology](https://github.com/darrenjw/smfsb/), and have a basic familiarity with [Scala](https://www.scala-lang.org/) and [Breeze](https://github.com/scalanlp/breeze). For those new to Scala, my course on [Scala for statistical computing](https://github.com/darrenjw/scala-course/blob/master/SelfStudyGuide.md) is a good place to start.

## Documentation

* [Tutorial](docs/Tutorial.md) - walk through of basic functionality
* There are more interesting examples in the [examples directory](examples/). To run them, download or clone the repo, and do `sbt run` from *inside* the examples directory.
* [Latest API docs](https://darrenjw.github.io/scala-smfsb/api/smfsb.html)
* I also have a couple of (oldish) blog posts: [An introduction to scala-smfsb](https://darrenjw.wordpress.com/2019/01/04/the-scala-smfsb-library/) - a "taster", and a post looking at [stochastic reaction-diffusion modelling](https://darrenjw.wordpress.com/2019/01/22/stochastic-reaction-diffusion-modelling/) using the library. But please note that these use an older version of the library and an older version of Scala, so a few details may need updating.

## Using the library in your own Scala projects

### giter8 template:

To create a new Scala `sbt` project template, just use:
```bash
sbt new darrenjw/scala-smfsb.g8
```
This will create a new project including a dependence on the stable version of the library.

### Stable:

Just add:
```scala
"com.github.darrenjw" %% "scala-smfsb" % "0.9"
```
to your `sbt` library dependencies. You might also need to add an explicit dependence on `breeze-viz`:
```scala
"org.scalanlp" %% "breeze-viz" % "2.0"
```

### Snapshot:

Just add something like:
```scala
libraryDependencies += "com.github.darrenjw" %% "scala-smfsb" % "1.0-SNAPSHOT"
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

