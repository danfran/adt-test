case class BasketError(id: String, message: String)
case class Discount(description: String, total: Double)


object ShoppingBasket {
  val builtinProductDatabase: Map[String, Int] = Map(
    "soup" -> 65,
    "bread" -> 80,
    "milk" -> 130,
    "apples" -> 100
  )

  val commands: Seq[String] = Seq("pricebasket")

  def parseCommandLine(commandLine: String): Either[BasketError, (String, Seq[String])] = {
    val splittedCommandLine = commandLine.trim.split(" ").filter(_.nonEmpty).map(_.toLowerCase()).toList

    splittedCommandLine match {
      case Nil => Left(BasketError("EMPTY_CMD", "Command line is empty"))
      case _ :: Nil => Left(BasketError("EMPTY_CMD", "Missing basket items"))
      case command :: items => Right((command, items))
    }
  }

  def validateCommandLine(command: String, items: Seq[String], productDatabase: Map[String, Float]): Either[BasketError, (String, Seq[String])] =
    for {
      validatedCommand <- Either.cond(commands.contains(command), command, BasketError("BAD_COMMAND", "Unexisting command"))

      _ <- items.foldLeft[Either[BasketError, List[String]]](Right(List.empty)) { (accEither, item) =>
        accEither.flatMap { accList =>
          productDatabase.get(item).toRight(BasketError("ITEM_NOT_FOUND", s"The item '$item' does not exist"))
            .map(_ => accList :+ item)
        }
      }
    } yield (validatedCommand, items)

  def groupBasketItems (items: Seq[String]): Map[String, Int] =
    items.groupBy(identity).view.mapValues(_.length).toMap

  // extra safety that applies the discount only if "apples" exists as product
  def applyApplesDiscount(groupedItems: Map[String, Int], productDatabase: Map[String, Int]): Option[Discount] =
    for {
      applesPrice <- productDatabase.get("apples")
      numberOfApples <- groupedItems.get("apples")
    } yield Discount(
      description = "Apples 10% off",
      total = BigDecimal(.1 * numberOfApples * applesPrice).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    )

  def applySoupAndBreadDiscount(groupedItems: Map[String, Int], productDatabase: Map[String, Int]): Option[Discount] =
    for {
      breadPrice <- productDatabase.get("bread")
      _ <- productDatabase.get("soup") // check if soup is an existing product
      numberOfBreads <- groupedItems.get("bread")
      numberOfTinSoups <- groupedItems.get("soup")
    } yield Discount(
      description = "Buy 2 tins of soup and get a loaf of bread for half price",
      total = BigDecimal(.5 * Math.min(numberOfBreads, numberOfTinSoups / 2) * breadPrice)
        .setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    )

}
