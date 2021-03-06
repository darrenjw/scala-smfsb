name := "scala-smfsb"
organization := "com.github.darrenjw"
version := "0.9-SNAPSHOT"

// enablePlugins(TutPlugin)

mdocIn := file("mdoc/")
mdocOut := file("docs/")
enablePlugins(MdocPlugin)

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature", "-language:higherKinds"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.2" % "test",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.0",
  "org.scalanlp" %% "breeze" % "2.0-SNAPSHOT",
  "org.scalanlp" %% "breeze-viz" % "2.0-SNAPSHOT",
  "org.scalanlp" %% "breeze-natives" % "2.0-SNAPSHOT"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// scalaVersion := "2.12.10"

scalaVersion := "2.13.4"

// crossScalaVersions := Seq("2.11.11", "2.12.10")


ThisBuild / scalaVersion := "2.12.10" // for ensime...


// eof


