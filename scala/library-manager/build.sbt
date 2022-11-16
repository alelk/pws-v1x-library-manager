ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "pws-library-manager",
    idePackagePrefix := Some("com.alelk.pws.library_manager")
  )

projectDependencies += "com.github.geirolz" %% "advxml-core" % "2.5.1"

// test dependencies
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"
libraryDependencies += "com.github.dwickern" %% "scala-nameof" % "4.0.0" % "test"
