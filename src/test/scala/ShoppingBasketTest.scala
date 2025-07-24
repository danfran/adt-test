import ShoppingBasket.{calculateSubTotalPrice, groupBasketItems}
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
}
