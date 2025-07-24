import DiscountsToBeApplied.DiscountToBeApplied
import InputParser.{parseCommandLine, validateCommandLine}

case class ProcessedBasket(appliedDiscounts: Seq[Discount], subTotal: Int)

object ShoppingBasket {

  def groupBasketItems (items: Seq[String]): Map[String, Int] =
    items.groupBy(identity).view.mapValues(_.length).toMap

  def calculateSubTotalPrice(groupedItems: Map[String, Int], productDatabase: Map[String, Int]): Int =
    groupedItems.foldLeft(0) {
      case (tot, (item, quantity)) => tot + productDatabase.getOrElse(item, 0) * quantity
    }

  def processBasket(commandLine: String, productDatabase: Map[String, Int], discounts: Seq[DiscountToBeApplied]): Either[ParserError, ProcessedBasket] =
    for {
      parsedCommandLine <- parseCommandLine(commandLine)
      (command, items) = parsedCommandLine
      validatedCommandLine <- validateCommandLine(command, items, productDatabase)
      (_, validateItems) = validatedCommandLine
      groupedItems = groupBasketItems(validateItems)
      appliedDiscounts = discounts.foldLeft(Seq.empty[Discount]) {
        case (applied, discount: DiscountToBeApplied) =>
          val d: Option[Discount] = discount(groupedItems, productDatabase)
          d match {
            case Some(v) if v.total > 0 => applied ++ d
            case _ => applied
          }
      }
      subTotal = calculateSubTotalPrice(groupedItems, productDatabase)
    } yield ProcessedBasket(appliedDiscounts, subTotal)

}
