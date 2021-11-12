name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

val http4sVersion = "0.23.6"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",

  "org.tpolecat" %% "doobie-core" % "0.13.4",
//  "org.tpolecat" %% "doobie-postgres" % "0.8.8",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",

  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

  "org.sangria-graphql" %% "sangria" % "2.1.5",
)
