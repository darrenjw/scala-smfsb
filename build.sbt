name := "scala-smfsb"
organization := "com.github.darrenjw"
version := "0.2-SNAPSHOT"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalanlp" %% "breeze" % "0.13.2",
  "org.scalanlp" %% "breeze-viz" % "0.13.2",
  "org.scalanlp" %% "breeze-natives" % "0.13.2"
)



scalaVersion := "2.12.6"
//scalaVersion := "2.11.11"

crossScalaVersions := Seq("2.11.11","2.12.6")





// eof


