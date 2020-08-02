
name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.0-M1",
  "io.circe" %% "circe-parser" % "0.14.0-M1",
  "io.circe" %% "circe-optics" % "0.13.0",

  "org.scalatest" %% "scalatest" % "3.2.0" % "test",
  //Cats
  "org.typelevel" %% "cats-core" % "2.1.1",
  //Doobie
  "org.tpolecat" %% "doobie-core" % "0.8.8",
  "org.tpolecat" %% "doobie-hikari" % "0.8.8", // HikariCP transactor.
  "org.tpolecat" %% "doobie-hikari" % "0.8.8", // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres" % "0.8.8", // Postgres driver 42.2.9 + type mappings.
  "org.tpolecat" %% "doobie-quill" % "0.8.8", // Support for Quill 3.4.10
  "org.tpolecat" %% "doobie-specs2" % "0.8.8" % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "0.8.8" % "test", // ScalaTest support for typechecking statements.

  "org.sangria-graphql" %% "sangria" % "2.0.0-M1",
  "org.sangria-graphql" %% "sangria-slowlog" % "2.0.0-M1",
  "org.sangria-graphql" %% "sangria-circe" % "1.3.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.2" % Test,

  "com.typesafe.akka" %% "akka-http" % "10.1.9",
  "de.heikoseeberger" %% "akka-http-circe" % "1.28.0"
)

