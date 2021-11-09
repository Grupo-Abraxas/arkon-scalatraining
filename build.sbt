name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"



lazy val catsEffectVersion    = "2.1.3"
lazy val catsVersion          = "2.1.1"
lazy val circeVersion         = "0.13.0"
lazy val doobieVersion        = "0.9.0"
lazy val fs2Version           = "2.3.0"
lazy val kindProjectorVersion = "0.9.10"
lazy val log4catsVersion      = "1.1.1"
lazy val sangriaCirceVersion  = "1.3.0"
lazy val sangriaVersion       = "2.1.3"
lazy val scala12Version       = "2.12.11"
lazy val http4sVersion        = "0.21.4"
lazy val slf4jVersion         = "1.7.32"
lazy val akkaVersion          = "2.5.26"
lazy val akkaHttpVersion      = "10.1.11"

libraryDependencies ++= Seq(

  "org.typelevel"        %% "cats-core"           % catsVersion,
  "org.typelevel"        %% "cats-effect"         % catsEffectVersion,
  "co.fs2"               %% "fs2-core"            % fs2Version,
  "co.fs2"               %% "fs2-io"              % fs2Version,
  "org.sangria-graphql"  %% "sangria"             % sangriaVersion,
  "org.sangria-graphql"  %% "sangria-circe"       % sangriaCirceVersion,

  "org.tpolecat"         %% "doobie-core"         % doobieVersion,
  "org.tpolecat"         %% "doobie-postgres"     % doobieVersion,
  "org.tpolecat"         %% "doobie-hikari"       % doobieVersion,
  "org.tpolecat"         %% "doobie-quill"        % doobieVersion,          // Support for Quill 3.7.2
  "org.tpolecat"         %% "doobie-specs2"       % doobieVersion % "test", // Specs2 support for typechecking statements.
  "org.tpolecat"         %% "doobie-scalatest"    % doobieVersion % "test",  // ScalaTest support for typechecking statements.

  "org.http4s"           %% "http4s-dsl"          % http4sVersion,
  "org.http4s"           %% "http4s-blaze-server" % http4sVersion,
  "org.http4s"           %% "http4s-circe"        % http4sVersion,
  "io.circe"             %% "circe-optics"        % circeVersion,
  "io.chrisdavenport"    %% "log4cats-slf4j"      % log4catsVersion,
  "org.slf4j"            %  "slf4j-simple"        % slf4jVersion,

  "net.postgis" % "postgis-jdbc" % "2.3.0",
  "org.sangria-graphql" %% "sangria-spray-json"   % "1.0.2",

  // akka streams
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  // akka http
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,


  "org.scalatest" %% "scalatest"    % "3.2.0" % "test"
)
