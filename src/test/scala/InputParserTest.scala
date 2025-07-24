import InputParser.{parseCommandLine, validateCommandLine}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.TableDrivenPropertyChecks.forAll
import org.scalatest.prop.Tables.Table


class InputParserTest extends AnyFunSuite {

  test("should parse the command line") {
    val tests = Table(
      ("commandLine", "expected"),
      ("SomeCommand apple banana orange", Right("somecommand", Seq("apple", "banana", "orange"))),
      ("SomeCommand  apple    banana ", Right("somecommand", Seq("apple", "banana"))),
      ("SomeCommand", Left(ParserError("EMPTY_CMD", "Missing basket items"))),
      ("", Left(ParserError("EMPTY_CMD", "Command line is empty"))),
    )

    forAll(tests)  { (commandLine, expected) =>
      assert(parseCommandLine(commandLine) == expected)
    }
  }

  test("should validate the command line") {
    val mockDB: Map[String, Int] = Map(
      "soup" -> 10,
      "bread" -> 20,
      "milk" -> 30,
      "apples" -> 40
    )

    val tests = Table(
      ("cmd", "items", "db", "expected"),
      ("pricebasket", Seq("soup", "bread", "milk"), mockDB, Right("pricebasket", Seq("soup", "bread", "milk"))),
      ("pricebasket", Seq("mango"), mockDB, Left(ParserError("ITEM_NOT_FOUND", s"The item 'mango' does not exist"))),
      // to verify "fail fast"
      ("pricebasket", Seq("mango", "banana"), mockDB, Left(ParserError("ITEM_NOT_FOUND", s"The item 'mango' does not exist"))),
      ("pricebasket", Seq("banana", "mango"), mockDB, Left(ParserError("ITEM_NOT_FOUND", s"The item 'banana' does not exist"))),
      ("somecommand", Seq.empty[String], mockDB, Left(ParserError("BAD_COMMAND", "Unexisting command"))),
    )

    forAll(tests)  { (cmd, items, db, expected) =>
      assert(validateCommandLine(cmd, items, db) == expected)
    }
  }

}
