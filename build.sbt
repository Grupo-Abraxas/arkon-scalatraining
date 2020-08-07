name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.12.2"

lazy val global = project
  .in(file("."))
  .aggregate(
    common,
    scraper,
    training
  )

lazy val common = project
  .settings(
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val scraper = project
  .settings(
    settings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.requests
    )
  )
  .dependsOn(
    common
  )

lazy val training = project
  .settings(
    settings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )

lazy val settings = Seq(
  scalacOptions ++= Seq(
    "-unchecked",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-deprecation",
    "-encoding",
    "utf8"
  ),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val commonDependencies = Seq(
  dependencies.circeCore,
  dependencies.circeParser,
  dependencies.cirseGeneric,
  dependencies.akkaHttp,
  dependencies.akkaSpray,
  dependencies.akkaStream,
  dependencies.sangria,
  dependencies.sangriaSpray,
  dependencies.slf4j,
  dependencies.scalaTest,
  dependencies.catsCore,
  dependencies.doobieCore,
  dependencies.doobieHikari,
  dependencies.doobiePostgres
)

lazy val dependencies =
  new {
    val test = "test"
    val circeCoreV = "0.9.3"
    val circeGenericV = "0.13.0"
    val sangriaV = "1.2.2"
    val sangriaSprayV = "1.0.0"
    val akkaV = "10.1.11"
    val akkaStreamV = "2.6.8"
    val slf4jV = "1.6.4"
    val scalaTestV = "3.2.0"
    val catsCoreV = "2.1.1"
    val doobieV = "0.8.8"
    val requestsV = "0.6.5"

    val circeCore = "io.circe" %% "circe-core" % circeCoreV
    val circeParser = "io.circe" %% "circe-parser" % circeCoreV
    val cirseGeneric = "io.circe" %% "circe-generic" % circeGenericV
    val sangria = "org.sangria-graphql" %% "sangria" % sangriaV
    val sangriaSpray = "org.sangria-graphql" %% "sangria-spray-json" % sangriaSprayV
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaV
    val akkaSpray = "com.typesafe.akka" %% "akka-http-spray-json" % akkaV
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaStreamV
    val slf4j = "org.slf4j" % "slf4j-nop" % slf4jV
    val scalaTest = "org.scalatest" %% "scalatest" % scalaTestV % test
    val catsCore = "org.typelevel" %% "cats-core" % catsCoreV
    val doobieCore = "org.tpolecat" %% "doobie-core" % doobieV
    val doobieHikari = "org.tpolecat" %% "doobie-hikari" % doobieV
    val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieV
    val requests = "com.lihaoyi" %% "requests" % requestsV
  }


