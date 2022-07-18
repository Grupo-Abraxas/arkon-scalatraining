name := "arkon-scala-training"

version := "0.0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "io.circe"            %% "circe-generic"         % "0.14.2",
  "io.circe"            %% "circe-literal"         % "0.14.2",
  "io.circe"            %% "circe-optics"          % "0.14.1",
  "org.http4s"          %% "http4s-dsl"            % "0.23.13",
  "org.http4s"          %% "http4s-ember-server"   % "0.23.13",
  "org.http4s"          %% "http4s-ember-client"   % "0.23.13",
  "org.http4s"          %% "http4s-circe"          % "0.23.13",
  "org.tpolecat"        %% "doobie-core"           % "1.0.0-RC1",
  "org.tpolecat"        %% "doobie-hikari"         % "1.0.0-RC1",
  "org.tpolecat"        %% "doobie-postgres"       % "1.0.0-RC1",
  "org.tpolecat"        %% "doobie-postgres-circe" % "1.0.0-RC1",
  "org.tpolecat"        %% "doobie-specs2"         % "1.0.0-RC1" % "test",
  "org.tpolecat"        %% "doobie-scalatest"      % "1.0.0-RC1" % "test",
  "org.typelevel"       %% "cats-effect"           % "3.3.12",
  "net.postgis"          % "postgis-jdbc"          % "2.3.0",
  "org.sangria-graphql" %% "sangria"               % "2.0.0",
  "org.sangria-graphql" %% "sangria-circe"         % "1.3.0",
  "org.slf4j"            % "slf4j-nop"             % "1.6.4"
)
