case class Discount(description: String, total: Int)

object Discounts {

  def applesDiscount10Percent(groupedItems: Map[String, Int], productDatabase: Map[String, Int]): Option[Discount] =
    for {
      applesPrice <- productDatabase.get("apples")
      numberOfApples <- groupedItems.get("apples")
    } yield Discount(
      description = "Apples 10% off",
      total = BigDecimal(.1 * numberOfApples * applesPrice).setScale(2, BigDecimal.RoundingMode.HALF_UP).toInt
    )

  def twoTinsSoupFor50PercentBreadDiscount(groupedItems: Map[String, Int], productDatabase: Map[String, Int]): Option[Discount] =
    for {
      breadPrice <- productDatabase.get("bread")
      _ <- productDatabase.get("soup") // check if soup is an existing product
      numberOfBreads <- groupedItems.get("bread")
      numberOfTinSoups <- groupedItems.get("soup")
    } yield Discount(
      description = "Buy 2 tins of soup and get a loaf of bread for half price",
      total = BigDecimal(.5 * Math.min(numberOfBreads, numberOfTinSoups / 2) * breadPrice)
        .setScale(2, BigDecimal.RoundingMode.HALF_UP).toInt
    )
}

object DiscountsToBeApplied {
  import Discounts._

  type DiscountToBeApplied = (Map[String, Int], Map[String, Int]) => Option[Discount]

  val allDiscounts: Seq[DiscountToBeApplied] = Seq(
    twoTinsSoupFor50PercentBreadDiscount,
    applesDiscount10Percent,
  )
}