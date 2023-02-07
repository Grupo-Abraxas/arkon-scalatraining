package training.graphql

import scala.util.{Failure, Success}

import sangria.macros._
import sangria.ast.Document
import sangria.parser.QueryParser

/** Fabrica para instancias de [[training.graphql.Parser]]. */
object Parser {
    /** Usa la definición de sangria para parsear un query
     *
     *  @param qs query en formato string
     *  @return Either.
     */
    def parse(qs: String): Either[Throwable, Document] = QueryParser.parse(qs) match {
        case Success(document) => Right(document)
        case Failure(error) => Left(error)
    }
}
