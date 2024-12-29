name := "scala-smfsb"
organization := "com.github.darrenjw"
version := "1.2-SNAPSHOT"

mdocIn := file("mdoc/")
mdocOut := file("docs/")
enablePlugins(MdocPlugin)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:higherKinds"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % "test",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
  "org.scalanlp" %% "breeze" % "2.1.0",
  "org.scalanlp" %% "breeze-viz" % "2.1.0"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

scalaVersion := "3.3.4"

crossScalaVersions := Seq("2.13.15", "3.3.4")

// eof
