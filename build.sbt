name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.typelevel" %% "cats-effect" % "2.1.3",
  "co.fs2" %% "fs2-core" % "2.3.0",
  "co.fs2" %% "fs2-io" % "2.3.0",
  "org.sangria-graphql" %% "sangria" % "2.1.4",
  "org.sangria-graphql" %% "sangria-circe" % "1.3.0",
  "io.circe" %% "circe-core" % "0.13.0",
  "io.circe" %% "circe-optics" % "0.13.0",
  "io.circe" %% "circe-parser" % "0.13.0",
  "org.tpolecat" %% "doobie-core" % "0.9.0",
  "org.tpolecat" %% "doobie-postgres" % "0.9.0",
  "org.tpolecat" %% "doobie-hikari" % "0.9.0",
  "org.http4s" %% "http4s-dsl" % "0.21.4",
  "org.http4s" %% "http4s-blaze-server" % "0.21.4",
  "org.http4s" %% "http4s-circe" % "0.21.4",
  "org.scalatest" %% "scalatest" % "3.2.0" % "test",
  "org.slf4j" % "slf4j-simple" % "1.7.30"
)
