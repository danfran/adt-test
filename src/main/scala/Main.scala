import java.text.NumberFormat
import java.util.Locale
import ShoppingBasket.processBasket

import scala.io.StdIn

trait CurrencyUtils {
  private val ukLocale = new Locale("en", "GB")

  def formatUKCurrency(price: Int): String = {
    if (price >= 0 && price < 100) {
      s"${price}p"
    } else {
      val majorUnitAmount = BigDecimal(price) / 100.0
      val currencyFormatter = NumberFormat.getCurrencyInstance(ukLocale)

      currencyFormatter.format(majorUnitAmount)
    }
  }
}

object Main extends App with CurrencyUtils {
  private def printBill(processedBasket: ProcessedBasket): Unit = {
    var totalPrice = processedBasket.subTotal

    println(s"Subtotal: ${formatUKCurrency(totalPrice)}")

    if (processedBasket.appliedDiscounts.isEmpty) {
      println("(No offers available)")
    } else {
      processedBasket.appliedDiscounts.foreach {
        discount =>
          println(s"${discount.description}: ${formatUKCurrency(discount.total)}")
          totalPrice -= discount.total
      }
    }

    println(s"Total Price: ${formatUKCurrency(totalPrice)}")
  }

  private def printError(parserError: ParserError): Unit = {
    println("The bill cannot be processed due to the following ->>")
    println(s"Error[${parserError.id}]: ${parserError.message}")
  }

  // the money currency is in pennies, e.g. £1 = 100p
  private val builtinProductDatabase: Map[String, Int] = Map(
    "soup" -> 65,
    "bread" -> 80,
    "milk" -> 130,
    "apples" -> 100
  )

  while(true) {
    print(">>> ")
    val commandLine = StdIn.readLine()

    val processedBasket: Either[ParserError, ProcessedBasket] =
      processBasket(commandLine, builtinProductDatabase, DiscountsToBeApplied.allDiscounts)

    processedBasket match {
      case Right(processedBasket) => printBill(processedBasket)
      case Left(parserError) => printError(parserError)
    }
  }
}

