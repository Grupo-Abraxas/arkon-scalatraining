# arkon-scalatraining

## Concepts
- FP
    - [Why functional programming?](http://book.realworldhaskell.org/read/why-functional-programming-why-haskell.html)
- Referential transparency.
- Immutability.
- Recursion (FP).
- Basic concurrency using [Scala's Future](https://docs.scala-lang.org/overviews/core/futures.html). 
- Functor/Mondad (FP through [cats](https://typelevel.org/cats/)).
- The real world/side effects using the [IO Monad](http://book.realworldhaskell.org/read/io.html).
- Testing

## Tools
- [Scala](https://www.scala-lang.org/2020/06/29/one-click-install.html)
- [IntelliJ (IDE)](https://www.jetbrains.com/idea/download/)
- [sbt](https://www.scala-sbt.org/)

## Libraries
- [cats](https://typelevel.org/cats/): Library for FP.
- [cats-effect](https://typelevel.org/cats-effect/): IO Monad in Scala.
- [FS2](https://fs2.io/index.html): Functional streams.
- [Doobie](https://tpolecat.github.io/doobie/): Functional layer for JDBC.
- [Sangria](https://sangria-graphql.org/): Scala library for GraphQL.
- [ScalaTest](https://www.scalatest.org/): Scala testing library.
- [ScalaCheck](https://www.scalacheck.org/): Library for random testing of program properties inspired by [QuickCheck](https://hackage.haskell.org/package/QuickCheck).



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
- web service
- 
