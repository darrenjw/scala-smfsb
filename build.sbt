name := "scala-smfsb"
organization := "com.github.darrenjw"
version := "0.8-SNAPSHOT"

// enablePlugins(TutPlugin)

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature", "-language:higherKinds"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.0",
  "org.scalanlp" %% "breeze" % "1.1",
  "org.scalanlp" %% "breeze-viz" % "1.1",
  "org.scalanlp" %% "breeze-natives" % "1.1"
)



// scalaVersion := "2.12.10"

scalaVersion := "2.13.4"

// crossScalaVersions := Seq("2.11.11", "2.12.10")


scalaVersion in ThisBuild := "2.12.10" // for ensime...


// eof


