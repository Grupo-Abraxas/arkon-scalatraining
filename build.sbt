ThisBuild / scalaVersion := "3.6.4"

ThisBuild / scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:noAutoTupling",
  "-Wnonunit-statement",
  "-Wsafe-init",
  "-Wunused:all",
  "-Wvalue-discard",
  "-Xfatal-warnings",
  "-Xkind-projector",
  "-Yexplicit-nulls"
)

ThisBuild / outputStrategy := Some(StdoutOutput)

ThisBuild / organization := "com.arkondata"

ThisBuild / credentials += Credentials(
  "arkondata/sbt-dev",
  "arkondata-744752950324.d.codeartifact.us-east-1.amazonaws.com",
  "aws",
  sys.env.getOrElse("CODEARTIFACT_AUTH_TOKEN", "")
)

ThisBuild / resolvers += "arkondata--sbt-dev".at(
  "https://arkondata-744752950324.d.codeartifact.us-east-1.amazonaws.com/maven/sbt-dev"
)

val catsEffectVersion = "3.6.0"

val circeVersion = "0.14.12"

val cirisVersion = "3.7.0"

val http4sVersion = "0.23.30"

val munitCatsEffectVersion = "2.0.0"

val sangriaVersion = "4.2.5"

val sangriaCirceVersion = "1.3.2"

val skunkVersion = "0.6.4"

addCommandAlias("format", "scalafixEnable;scalafixAll;scalafmtAll;scalafmtSbt")

lazy val root =
  project.in(file(".")).aggregate(training)

lazy val training =
  project
    .in(file("training"))
    .settings(
      name := "arkon-training",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-effect" % catsEffectVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion,
        "is.cir" %% "ciris-http4s" % cirisVersion,
        "org.http4s" %% "http4s-ember-server" % http4sVersion,
        "org.sangria-graphql" %% "sangria" % sangriaVersion,
        "org.sangria-graphql" %% "sangria-circe" % sangriaCirceVersion,
        "org.tpolecat" %% "skunk-core" % skunkVersion,
        "org.typelevel" %% "munit-cats-effect" % munitCatsEffectVersion % Test,
				"org.http4s" %% "http4s-dsl" % http4sVersion,
				"org.http4s" %% "http4s-circe" % http4sVersion
			)
    )
