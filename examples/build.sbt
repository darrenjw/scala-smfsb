name := "scala-smfsb-examples"

version := "0.3-SNAPSHOT"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.2" % "test",
  "org.scalanlp" %% "breeze" % "1.1",
  "org.scalanlp" %% "breeze-viz" % "1.1",
  "org.scalanlp" %% "breeze-natives" % "1.1",
  "com.github.darrenjw" %% "scala-smfsb" % "0.8-SNAPSHOT"
)

resolvers ++= Seq(
  "Sonatype Snapshots" at
    "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at
    "https://oss.sonatype.org/content/repositories/releases/"
)

// scalaVersion := "2.12.10"

scalaVersion := "2.13.4"

//scalaVersion in ThisBuild := "2.12.10" // for ensime


