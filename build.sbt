name := "scala-smfsb"
organization := "com.github.darrenjw"
version := "0.1-SNAPSHOT"

scalacOptions ++= Seq(
  "-unchecked", "-deprecation", "-feature"
)

libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalanlp" %% "breeze" % "0.13",
  "org.scalanlp" %% "breeze-viz" % "0.13",
  "org.scalanlp" %% "breeze-natives" % "0.13"
)



scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.11","2.12.1")

// publishTo := Some(Resolver.sftp("Personal mvn repo", "unix.ncl.ac.uk", "/home/ucs/100/ndjw1/public_html/mvn"))




// eof


