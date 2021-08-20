name := "scala-smfsb"
organization := "com.github.darrenjw"
version := "0.9-SNAPSHOT"

mdocIn := file("mdoc/")
mdocOut := file("docs/")
enablePlugins(MdocPlugin)

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature", "-language:higherKinds"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3",
  "org.scalanlp" %% "breeze" % "2.0-RC3",
  "org.scalanlp" %% "breeze-viz" % "2.0-RC3",
  "org.scalanlp" %% "breeze-natives" % "2.0-RC3"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"


//scalaVersion := "2.13.5"
scalaVersion := "3.0.1"

crossScalaVersions := Seq("2.13.5", "3.0.1")


// ThisBuild / scalaVersion := "2.12.10" // for ensime...


// eof


