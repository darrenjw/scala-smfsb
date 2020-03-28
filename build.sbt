name := "scala-smfsb"
organization := "com.github.darrenjw"
version := "0.7-SNAPSHOT"

enablePlugins(TutPlugin)

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature", "-language:higherKinds"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.scalanlp" %% "breeze" % "1.0",
  "org.scalanlp" %% "breeze-viz" % "1.0",
  "org.scalanlp" %% "breeze-natives" % "1.0"
)



scalaVersion := "2.12.10"

crossScalaVersions := Seq("2.11.11", "2.12.10")


scalaVersion in ThisBuild := "2.12.10" // for ensime...


// eof


