name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "org.tpolecat"             %% "doobie-core" % "1.0.0-RC2",
  "org.tpolecat"             %% "doobie-hikari" % "1.0.0-RC2",
  "org.tpolecat"             %% "doobie-postgres" % "1.0.0-RC2",

  "org.sangria-graphql" %% "sangria"               % "3.0.0",
  "org.sangria-graphql" %% "sangria-ast"           % "3.0.0",
  "org.sangria-graphql" %% "sangria-parser"        % "3.0.0",
  "org.sangria-graphql" %% "sangria-core"          % "3.0.0",
  "org.sangria-graphql" %% "sangria-derivation"    % "3.0.0",
  "org.sangria-graphql" %% "sangria-circe"         % "1.3.2",
  "org.sangria-graphql" %% "sangria-spray-json"    % "1.0.2",

  "io.circe" %% "circe-core"    % "0.14.2",
  "io.circe" %% "circe-generic" % "0.14.2",
  "io.circe" %% "circe-parser"  % "0.14.2",

   "org.http4s" %% "http4s-ember-server" % "0.23.12",
   "org.http4s" %% "http4s-ember-client" % "0.23.12",
   "org.http4s" %% "http4s-circe"        % "0.23.12",
   "org.http4s" %% "http4s-dsl"          % "0.23.12",

  "org.typelevel"        %% "cats-core"           % "2.7.0",
  "org.typelevel"        %% "cats-effect"         % "3.3.12",

  "org.scalatest" %% "scalatest"    % "3.2.12" % "test"
)
