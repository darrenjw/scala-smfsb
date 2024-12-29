name := "scala-smfsb-examples"

version := "1.0"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % "test",
  "org.scalanlp" %% "breeze" % "2.1.0",
  "org.scalanlp" %% "breeze-viz" % "2.1.0",
  "com.github.darrenjw" %% "scala-smfsb" % "1.1"
)

resolvers ++= Seq(
  "Sonatype Snapshots" at
    "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at
    "https://oss.sonatype.org/content/repositories/releases/"
)

scalaVersion := "3.3.4"
