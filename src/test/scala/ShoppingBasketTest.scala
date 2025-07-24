import DiscountsToBeApplied.allDiscounts
import ShoppingBasket.{calculateSubTotalPrice, groupBasketItems, processBasket}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.TableDrivenPropertyChecks.forAll
import org.scalatest.prop.Tables.Table


class ShoppingBasketTest extends AnyFunSuite {

  test("should group the basket items properly") {
    val items = List("soup", "bread", "soup", "soup", "milk", "soup", "milk")
    assert(groupBasketItems(items) == Map("milk" -> 2, "bread" -> 1, "soup" -> 4))
  }

  test("should calculate the basket subtotal") {
    val mockDB: Map[String, Int] = Map("soup" -> 65, "bread" -> 80, "milk" -> 130, "apples" -> 100)

    val tests = Table(
      ("groupedItems", "db", "expected"),
      (Map.empty[String, Int], mockDB, 0),
      (Map("milk" -> 1, "unknown" -> 1, "soup" -> 4), mockDB, 390),
      (Map("milk" -> 2, "bread" -> 1, "soup" -> 4), mockDB, 600),
    )

    forAll(tests)  { (groupedItems, db, expected) =>
      assert(calculateSubTotalPrice(groupedItems, db) == expected)
    }
  }

  test("should process the basket") {
    val mockDB: Map[String, Int] = Map("soup" -> 65, "bread" -> 80, "milk" -> 130, "apples" -> 100)

    val tests = Table(
      ("commandLine", "db", "discounts", "expected"),
      ("", mockDB, allDiscounts, Left(ParserError("EMPTY_CMD", "Command line is empty"))),
      ("       ", mockDB, allDiscounts, Left(ParserError("EMPTY_CMD", "Command line is empty"))),
      ("PriceBasket ", mockDB, allDiscounts, Left(ParserError("EMPTY_CMD", "Missing basket items"))),
      ("PrirceBasket Apples Milk Bread Soup Soupss", mockDB, allDiscounts, Left(ParserError("BAD_COMMAND", "Unknown command 'prircebasket'"))),
      ("PriceBasket Apples Milk Bread Soup Soupss", mockDB, allDiscounts, Left(ParserError("ITEM_NOT_FOUND", "The item 'soupss' does not exist"))),
      ("pricebasket milk ", mockDB, allDiscounts, Right(ProcessedBasket(List(), 130))),
      ("PriceBasket Milk ", mockDB, allDiscounts, Right(ProcessedBasket(List(), 130))),
      ("PriceBasket Apples Milk Bread", mockDB, allDiscounts, Right(ProcessedBasket(List(Discount("Apples 10% off", 10)), 310))),
      ("PriceBasket Apples Milk Bread Soup ", mockDB, allDiscounts, Right(
        ProcessedBasket(List(Discount("Apples 10% off", 10)), 375)
      )),
      ("PriceBasket Apples Milk Bread Soup Soup", mockDB, allDiscounts, Right(
        ProcessedBasket(List(Discount("Buy 2 tins of soup and get a loaf of bread for half price", 40), Discount("Apples 10% off", 10)), 440)
      )),
    )

    forAll(tests)  { (commandLine, db, discounts, expected) =>
      assert(processBasket(commandLine, db, discounts) == expected)
    }
  }
}
