scalaVersion := "2.13.5"
scalaVersion := "2.13.5"
name := "scala-smfsb-examples"

version := "0.9-SNAPSHOT"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "org.scalanlp" %% "breeze" % "2.0",
  "org.scalanlp" %% "breeze-viz" % "2.0",
  "org.scalanlp" %% "breeze-natives" % "2.0",
  "com.github.darrenjw" %% "scala-smfsb" % "0.9-SNAPSHOT"
)

resolvers ++= Seq(
  "Sonatype Snapshots" at
    "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at
    "https://oss.sonatype.org/content/repositories/releases/"
)


//scalaVersion := "2.13.5"
scalaVersion := "3.0.1"

//scalaVersion in ThisBuild := "2.12.10" // for ensime


