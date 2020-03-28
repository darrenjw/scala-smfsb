name := "scala-smfsb-examples"

version := "0.3-SNAPSHOT"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.scalanlp" %% "breeze" % "1.0",
  "org.scalanlp" %% "breeze-viz" % "1.0",
  "org.scalanlp" %% "breeze-natives" % "1.0",
  "com.github.darrenjw" %% "scala-smfsb" % "0.7-SNAPSHOT"
)

resolvers ++= Seq(
  "Sonatype Snapshots" at
    "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at
    "https://oss.sonatype.org/content/repositories/releases/"
)

scalaVersion := "2.12.10"

scalaVersion in ThisBuild := "2.12.10" // for ensime


