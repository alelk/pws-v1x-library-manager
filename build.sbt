import scala.io.Source
import scala.util.Using

ThisBuild / version := Using(Source.fromFile("app.version"))(_.mkString.trim).getOrElse("0.0.0")
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / organization := "com.alelk.pws"
ThisBuild / organizationName := "Alex Elkin"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "pws-library-manager-v1x",
    idePackagePrefix := Some("com.alelk.pws.library_manager")
  )

// xml
projectDependencies += "com.github.geirolz" %% "advxml-core" % "2.5.1"

// yaml
libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.2"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % "0.14.2")

// logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.14"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.3"

// test dependencies
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"
libraryDependencies += "com.github.dwickern" %% "scala-nameof" % "4.0.0" % "test"
libraryDependencies += "io.lemonlabs" %% "scala-uri" % "4.0.3"

publishTo := {
  val github = "https://maven.pkg.github.com/alelk/pws-v1x-library-manager"
  Some("GitHub Package Registry" at github)
}
publishMavenStyle := true
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