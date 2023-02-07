name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.tpolecat"         %% "doobie-core"         % "1.0.0-RC2",
  "org.tpolecat"         %% "doobie-hikari"       % "1.0.0-RC2",
  "org.tpolecat"         %% "doobie-postgres"     % "1.0.0-RC2",

  "org.sangria-graphql" %% "sangria-ast"           % "3.2.0",
  "org.sangria-graphql" %% "sangria-parser"        % "3.2.0",
  "org.sangria-graphql" %% "sangria-core"          % "3.2.0",
  "org.sangria-graphql" %% "sangria-derivation"    % "3.2.0",
  "org.sangria-graphql" %% "sangria-circe"         % "1.3.2",

  "io.circe" %% "circe-generic" % "0.14.3",
  "io.circe" %% "circe-parser"  % "0.14.3",

  "org.http4s" %% "http4s-blaze-server" % "0.23.13",
  "org.http4s" %% "http4s-blaze-client" % "0.23.13",
  "org.http4s" %% "http4s-circe"        % "0.23.13",
  "org.http4s" %% "http4s-dsl"          % "0.23.13",

  "org.postgis" % "postgis-jdbc" % "1.3.3",
  
  "org.scalatest" %% "scalatest"    % "3.2.9" % "test"
)
