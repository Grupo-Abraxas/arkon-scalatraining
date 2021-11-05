name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

val http4sVersion = "1.0-28-62b0fb2-SNAPSHOT"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.12.3",
  "io.circe" %% "circe-parser" % "0.12.3",
  "org.tpolecat" %% "doobie-core" % "0.8.8",
  "org.scalatest" %% "scalatest" % "3.2.0" % "test",
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion
)
