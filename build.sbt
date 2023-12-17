ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / versionScheme := Some("semver-spec")

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "pws-library-manager-v1x",
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


publishTo := {
  val github = "https://maven.pkg.github.com/alelk/pws-library-manager"
  if (isSnapshot.value)
    Some("snapshots" at github)
  else
    Some("releases" at github)
}

sys.env.get("GITHUB_TOKEN") match {
  case Some(token) =>
    credentials += Credentials(
      "GitHub Package Registry",
      "maven.pkg.github.com",
      "alelk",
      token
    )
  case None =>
    println("GITHUB_TOKEN is not defined")
    credentials ++= Seq()
}