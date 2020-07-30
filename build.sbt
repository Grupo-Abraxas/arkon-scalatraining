name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "io.circe"      %% "circe-core"   % "0.12.3",
  "io.circe"      %% "circe-parser" % "0.12.3",
  "org.tpolecat"  %% "doobie-core"  % "0.8.8",
  "org.scalatest" %% "scalatest"    % "3.2.0" % "test",
//Cats
  "org.typelevel" %% "cats-core" % "2.1.1",
// Start with this one
  "org.tpolecat" %% "doobie-core"      % "0.8.8",

// And add any of these as needed
  "org.tpolecat" %% "doobie-hikari"    % "0.8.8",          // HikariCP transactor.
  "org.tpolecat" %% "doobie-hikari"    % "0.8.8",          // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres"  % "0.8.8",          // Postgres driver 42.2.9 + type mappings.
  "org.tpolecat" %% "doobie-quill"     % "0.8.8",          // Support for Quill 3.4.10
  "org.tpolecat" %% "doobie-specs2"    % "0.8.8" % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "0.8.8" % "test"  // ScalaTest support for typechecking statements.
)
