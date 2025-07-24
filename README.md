# adt-test

## Compile & run

The Scala version in the SBT file is 2.13. If you are using a "global version" on your machine you need to verify the version in case of errors.
Alternatively there are tools like [SDKMan](https://sdkman.io/) to manage multiple JDK/SBT/Scala versions on your system.
Another alternative could be using Docker, but I didn't add a Dockerfile and it would be an overkill for this example.
If you have IntelliJ you should be able to import the project with `File -> New -> Project from Existing Sources...`.

If you have installed `sbt`, you can simply run the app with `sbt run`. If you are using IntelliJ you can use the *sbt-shell* in IntelliJ and simply using the command `run` in there.

Once the app is running you should be able to input your values on the prompt `>>>` and getting the result back, like for example:

```
>>> PriceBasket Apples Milk Bread
Subtotal: £3.10
Apples 10% off: 10p
Total Price: £3.00
>>> PriceBasket Apples Milk Bread Soup
Subtotal: £3.75
Apples 10% off: 10p
Total Price: £3.65
>>> Wrongstuff Apples Milk Bread Soup
The bill cannot be processed due to the following ->>
Error[BAD_COMMAND]: Unknown command 'wrongstuff'
>>> PRicebasket appleS Milk bREAD
Subtotal: £3.10
Apples 10% off: 10p
Total Price: £3.00
>>> ....
```

☝️ Note that the input parameters are considered case unsensitive.

## Run the tests

To run all tests from `sbt` just run the command `sbt test` or simply `test` from the *sbt-shell*.

All tests should pass.