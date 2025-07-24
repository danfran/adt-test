import Discounts.{applesDiscount10Percent, twoTinsSoupFor50PercentBreadDiscount}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.TableDrivenPropertyChecks.forAll
import org.scalatest.prop.Tables.Table


class DiscountsTest extends AnyFunSuite {

  test("should apply apples discount") {
    val dbWithoutApples: Map[String, Int] = Map("soup" -> 65, "bread" -> 80, "milk" -> 130)
    val dbWithApples = dbWithoutApples ++ Map("apples" -> 100)

    val groupedItemsWithoutApples: Map[String, Int] = Map("milk" -> 2, "bread" -> 1, "soup" -> 4)
    val groupedItemsWithApples: Map[String, Int] = groupedItemsWithoutApples ++ Map("apples" -> 3)

    val tests = Table(
      ("groupedItems", "db", "expected"),
      (groupedItemsWithoutApples, dbWithoutApples, None),
      (groupedItemsWithoutApples, dbWithApples, None),
      (groupedItemsWithApples, dbWithoutApples, None),
      (groupedItemsWithApples, dbWithApples, Some(Discount(description = "Apples 10% off", total = 30))),
      // tests to verify the discount price computation
      (Map("apples" -> 100), Map("apples" -> 100), Some(Discount(description = "Apples 10% off", total = 1000))),
      (Map("apples" -> 100), Map("apples" -> 20), Some(Discount(description = "Apples 10% off", total = 200))),
      (Map("apples" -> 15), Map("apples" -> 20), Some(Discount(description = "Apples 10% off", total = 30))),
    )

    forAll(tests)  { (groupedItems, db, expected) =>
      assert(applesDiscount10Percent(groupedItems, db) == expected)
    }
  }

  test("should apply soup and bread discount") {
    val dbWithoutBread: Map[String, Int] = Map("soup" -> 65, "milk" -> 130, "apples" -> 100)
    val dbWithoutSoup: Map[String, Int] = Map("bread" -> 80, "milk" -> 130, "apples" -> 100)
    val dbWithSoupAndBread = dbWithoutSoup ++ Map("soup" -> 65)

    val groupedItemsWithoutBread: Map[String, Int] = Map("milk" -> 2, "apples" -> 3, "soup" -> 4)
    val groupedItemsWithBreadAndSoup: Map[String, Int] = groupedItemsWithoutBread ++ Map("bread" -> 1)

    val groupedItemsWithoutSoup: Map[String, Int] = Map("milk" -> 2, "apples" -> 3, "bread" -> 1)

    val tests = Table(
      ("groupedItems", "db", "expected"),
      (groupedItemsWithoutBread, dbWithoutSoup, None),
      (groupedItemsWithoutBread, dbWithSoupAndBread, None),
      (groupedItemsWithBreadAndSoup, dbWithoutBread, None),
      (groupedItemsWithBreadAndSoup, dbWithoutSoup, None),
      (groupedItemsWithoutSoup, dbWithSoupAndBread, None),
      (groupedItemsWithBreadAndSoup, dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 40))),
      // tests to verify the discount price computation
      (Map("soup" -> 1, "bread" -> 1), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 0))),
      (Map("soup" -> 2, "bread" -> 1), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 40))),
      (Map("soup" -> 4, "bread" -> 1), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 40))),
      (Map("soup" -> 3, "bread" -> 2), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 40))),
      (Map("soup" -> 4, "bread" -> 2), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 80))),
      (Map("soup" -> 2, "bread" -> 2), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 40))),
      (Map("soup" -> 8, "bread" -> 2), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 80))),
      (Map("soup" -> 1, "bread" -> 8), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 0))),
      (Map("soup" -> 2, "bread" -> 8), dbWithSoupAndBread,
        Some(Discount(description = "Buy 2 tins of soup and get a loaf of bread for half price", total = 40))),
    )

    forAll(tests)  { (groupedItems, db, expected) =>
      assert(twoTinsSoupFor50PercentBreadDiscount(groupedItems, db) == expected)
    }
  }

}
