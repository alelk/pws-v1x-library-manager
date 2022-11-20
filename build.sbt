ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "pws-library-manager",
    idePackagePrefix := Some("com.alelk.pws.library_manager")
  )

// xml
projectDependencies += "com.github.geirolz" %% "advxml-core" % "2.5.1"

// logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.4"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.3"

// test dependencies
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"
libraryDependencies += "com.github.dwickern" %% "scala-nameof" % "4.0.0" % "test"
libraryDependencies += "io.lemonlabs" %% "scala-uri" % "4.0.3"
