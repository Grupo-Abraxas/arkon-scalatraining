package training.graphql

import scala.util.{Failure, Success}

import sangria.macros._
import sangria.ast.Document
import sangria.parser.QueryParser

object Parser {
    def parse(qs: String): Either[Throwable, Document] = QueryParser.parse(qs) match {
      case Success(document) => Right(document)
      case Failure(error) => Left(error)
    }
}
