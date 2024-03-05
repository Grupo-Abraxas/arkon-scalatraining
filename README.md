<a href="https://www.arkondata.com/">
    <img src="./img/logo.jpg" align="right" height="80">
</a>

# Arkon's Scala Training

## Description
Scala hands on training project. Looking to introduce new team members or anyone interested to the scala 
programming language and the way it's used within the ArkonData team. 

You'll implement a web server exposing a GraphQL API to expose business retrieved from the INEGI's API and 
query them based on their location.

* [Concepts](https://github.com/Grupo-Abraxas/arkon-scalatraining#concepts)
* [Tools](https://github.com/Grupo-Abraxas/arkon-scalatraining#tools)
* [Libraries](https://github.com/Grupo-Abraxas/arkon-scalatraining#libraries)
* [Exercises](https://github.com/Grupo-Abraxas/arkon-scalatraining#exercises)
* [Basic commands](https://github.com/Grupo-Abraxas/arkon-scalatraining#basic-commands)
* [Requirements](https://github.com/Grupo-Abraxas/arkon-scalatraining#requirements)

## Concepts
- [Functional Programming](https://www.baeldung.com/scala/functional-programming)
- Referential transparency.
- Immutability.
- Recursion (FP).
- Basic concurrency using [Scala's Future](https://docs.scala-lang.org/overviews/core/futures.html). 
- Functor/Mondad (FP through [cats](https://typelevel.org/cats/)).
- The real world/side effects using the [IO Monad](https://medium.com/walmartglobaltech/understanding-io-monad-in-scala-b495ca572174).
- Testing
- [Conventional Commit](https://www.conventionalcommits.org/en/v1.0.0/)

- [Types and type clases](https://scalac.io/blog/typeclasses-in-scala/)
- [Implicits](https://docs.scala-lang.org/tour/implicit-parameters.html)
- [hlists](https://www.scala-exercises.org/shapeless/heterogenous_lists)

## Books
- [https://underscore.io/books](https://underscore.io/books/)

## Videos
- [Let’s Code Real World App Using Purely Functional Techniques (in Scala)](https://rutube.ru/video/012e1f2034234f39be847754a38fdfc8/) | [deprecated link](https://youtu.be/m40YOZr1nxQ)

## Tools
- [Scala](https://www.scala-lang.org/2020/06/29/one-click-install.html)
- [IntelliJ (IDE)](https://www.jetbrains.com/idea/download/)
- [sbt](https://www.scala-sbt.org/)

## Libraries
- [cats](https://typelevel.org/cats/): Library for FP.
- [cats-effect](https://typelevel.org/cats-effect/): IO Monad in Scala.
- [FS2](https://fs2.io/index.html): Functional streams.
- [Doobie](https://tpolecat.github.io/doobie/): Functional layer for JDBC.
- [Sangria](https://sangria-graphql.github.io/): Scala library for GraphQL.
- [ScalaTest](https://www.scalatest.org/): Scala testing library.
- [ScalaCheck](https://www.scalacheck.org/): Library for random testing of program properties inspired by [QuickCheck](https://hackage.haskell.org/package/QuickCheck).
- [http4s](https://http4s.org/): Library fot HTTP

## Exercises
- [Std lib](https://www.scala-exercises.org/std_lib/asserts)
- [Fp in Scala](https://www.scala-exercises.org/fp_in_scala/getting_started_with_functional_programming)
- [Cats](https://www.scala-exercises.org/cats/semigroup)
- [Circe](https://www.scala-exercises.org/circe/Json)
- [Doobie](https://www.scala-exercises.org/doobie/connecting_to_database)
- [ScalaCheck](https://www.scala-exercises.org/scalacheck/properties)

## Basic commands
SBT console
```
$ sbt
```

Running sbt commands inside the SBT console
```
// sbt console
$ sbt

// Scala REPL
$ sbt console

// Compile the main module
sbt:arkon-scalatraining> compile

// Compile the test module
sbt:arkon-scalatraining> test:compile

// Run all tests
sbt:arkon-scalatraining> test

// Run a specific test
sbt:arkon-scalatraining> testOnly training.std.OptionSpec
```

## Requirements 
Implement a GraphQL API based on the given [schema](./schema.graphql) to expose the saved business and 
query them based on their location. The database to be used should be [PostgreSQL](www.postgresql.org) with the 
[PostGIS](http://postgis.net/) exitension to power the georeferenced queries. To fill the database you'll have 
to implement a web scrapper to retrieve data from the INEGI's DENUE [API](https://www.inegi.org.mx/servicios/api_denue.html) 
and execute the `createShop` mutation defined on the implemented API.

The implemented API should comply the following rules: 
- On the `createShop` mutation 
    - The `activity`, `stratum` and `shopType` fields should search for existing records on the `Activity`, 
      `Stratum` and `ShopType` tables and insert only if there is no previous record.
