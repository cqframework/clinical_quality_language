library SignatureOutputTests

define function OverloadTest(value Decimal): value
define function OverloadTest(left Decimal, right Decimal): left
define function MultipleOverloadTest(): 5
define function MultipleOverloadTest(value Integer): value
define function MultipleOverloadTest(value Decimal): value
define function MultipleOverloadTest(left Integer, right Integer): left
define function MultipleOverloadTest(left Decimal, right Decimal): right
define function MultipleOverloadTest(left Decimal, middle Integer, right Decimal): middle

define TestAdd: 5 + 5
define TestDateAdd: @2012-01-01 + 1 month
define TestDateTime: DateTime(2012, 1, 1)
define TestAvg: Avg({ 1, 2, 3, 4, 5 })
define TestDivide: 4 / 5
define TestIntegerOverload: OverloadTest(1)
define TestDecimalOverload: OverloadTest(1.0)
define TestMultipleOverload: MultipleOverloadTest()
define TestIntegerMultipleOverload: MultipleOverloadTest(1)
define TestDecimalMultipleOverload: MultipleOverloadTest(1, 2.0)
define TestIntegerAndDecimalMultipleOverload: MultipleOverloadTest(2.0, 1, 2.0)