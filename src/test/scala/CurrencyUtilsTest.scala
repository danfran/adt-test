import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.TableDrivenPropertyChecks.forAll
import org.scalatest.prop.Tables.Table


class CurrencyUtilsTest extends AnyFunSuite with CurrencyUtils {

  test("should format the uk currency") {

    val tests = Table(
      ("groupedItems", "expected"),
      (30, "30p"),
      (130, "£1.30"),
      (100, "£1.00"),
      (10000, "£100.00"),
      (10099, "£100.99"),
    )

    forAll(tests) { (priceInPennies, expected) =>
      assert(formatUKCurrency(priceInPennies) == expected)
    }
  }
}
