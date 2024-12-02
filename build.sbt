
enablePlugins(ScalafmtPlugin, ScalafixPlugin)

ThisBuild / scalafixDependencies ++= Seq(
  "com.github.liancheng" %% "organize-imports" % "0.6.0"
)

inThisBuild(
  List(
    scalaVersion := "2.13.3",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= List("-Ywarn-unused", "-Wunused")
  )
)

ThisBuild / scalafmtConfig := file(".scalafmt.conf")

ThisBuild / onLoad := (ThisBuild / onLoad).value.andThen { state =>
  "scalafixAll" :: state
}

lazy val commonSettings = Seq(
  organization := "com.arkondata",
  version := "0.1",
  scalaVersion := "2.13.3",
  Test / parallelExecution := true,
  Test / fork := true
)

lazy val root = (project in file("."))
  .aggregate(graphql, ingest)
  .settings(
    name := "arkon-scalatraining",
    ThisBuild / version := "0.1",
    ThisBuild / scalaVersion := "2.13.3",
    publish / skip := true
  )

lazy val graphql = (project in file("modules/graphql"))
  .settings(commonSettings)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "graphql",
    Compile / mainClass := Some("app.Main"),
    libraryDependencies ++= Seq(
      "org.tpolecat"             %% "doobie-core" % "1.0.0-RC2",
      "org.tpolecat"             %% "doobie-hikari" % "1.0.0-RC2",
      "org.tpolecat"             %% "doobie-postgres" % "1.0.0-RC2",

      "org.flywaydb" % "flyway-core" % "9.22.0",

      "org.sangria-graphql" %% "sangria-ast"           % "3.0.0",
      "org.sangria-graphql" %% "sangria-parser"        % "3.0.0",
      "org.sangria-graphql" %% "sangria-core"          % "3.0.0",
      "org.sangria-graphql" %% "sangria-derivation"    % "3.0.0",
      "org.sangria-graphql" %% "sangria-circe"         % "1.3.2",

      "org.typelevel" %% "cats-effect" % "3.3.5",

      "org.postgresql" % "postgresql" % "42.3.1",

      "com.github.pureconfig" %% "pureconfig" % "0.17.4",

      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser"  % "0.14.1",

      "org.http4s" %% "http4s-ember-server" % "0.23.11",
      "org.http4s" %% "http4s-circe" % "0.23.11",
      "org.http4s" %% "http4s-dsl" % "0.23.11",

      "ch.qos.logback" % "logback-classic" % "1.2.11",

      "org.scalatest" %% "scalatest" % "3.2.9" % Test
    ),
    Docker / dockerBaseImage := "openjdk:11-jre-slim",
    Docker / dockerExposedPorts := Seq(8080)
  )

lazy val ingest = (project in file("modules/ingest"))
  .settings(commonSettings)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "ingest",
    Compile / mainClass := Some("app.Main"),
    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig" % "0.17.4",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC2",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1",
      "org.http4s" %% "http4s-dsl" % "0.23.11",
      "org.http4s" %% "http4s-blaze-client" % "0.23.11",
      "org.scalatest" %% "scalatest" % "3.2.9" % Test
    ),
    Docker / dockerBaseImage := "openjdk:11-jre-slim",
    Docker / dockerExposedPorts := Seq(8081)
  )
