name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "io.circe"      %% "circe-core"   % "0.14.1",
  "io.circe"      %% "circe-parser" % "0.14.1",
  "org.tpolecat"  %% "doobie-core"  % "0.8.8",
  "org.scalatest" %% "scalatest"    % "3.2.0" % "test"

)
