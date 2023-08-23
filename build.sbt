ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "psql2solr"
  )

val catsVersion = "3.5.0" // "2.5.4" // "3.4.8"
libraryDependencies += "org.typelevel" %% "cats-effect" % catsVersion
ThisBuild / libraryDependencySchemes += "org.typelevel" %% "cats-effect" % "always"

val circeVersion = "0.14.2"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-yaml"
  // "io.circe" %% "circe-yaml-v12"
).map(_ % circeVersion)


libraryDependencies += "com.github.takezoe" %% "solr-scala-client" % "0.0.27"

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"
