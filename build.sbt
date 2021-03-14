name := "arkon-scalatraining"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "io.circe"      %% "circe-core"   % "0.12.3",
  "io.circe"      %% "circe-parser" % "0.12.3",
  "org.tpolecat"  %% "doobie-core"  % "0.12.1",
  "org.scalatest" %% "scalatest"    % "3.2.0" % "test"

)
