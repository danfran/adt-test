case class ParserError(id: String, message: String)

object InputParser {

  val commands: Seq[String] = Seq("pricebasket")

  def parseCommandLine(commandLine: String): Either[ParserError, (String, Seq[String])] = {
    val splittedCommandLine = commandLine.trim.split(" ").filter(_.nonEmpty).map(_.toLowerCase()).toList

    splittedCommandLine match {
      case Nil => Left(ParserError("EMPTY_CMD", "Command line is empty"))
      case _ :: Nil => Left(ParserError("EMPTY_CMD", "Missing basket items"))
      case command :: items => Right((command, items))
    }
  }

  def validateCommandLine(command: String, items: Seq[String], productDatabase: Map[String, Int]): Either[ParserError, (String, Seq[String])] =
    for {
      validatedCommand <- Either.cond(commands.contains(command), command, ParserError("BAD_COMMAND", s"Unknown command '$command'"))

      _ <- items.foldLeft[Either[ParserError, List[String]]](Right(List.empty)) { (accEither, item) =>
        accEither.flatMap { accList =>
          productDatabase.get(item).toRight(ParserError("ITEM_NOT_FOUND", s"The item '$item' does not exist"))
            .map(_ => accList :+ item)
        }
      }
    } yield (validatedCommand, items)
}
