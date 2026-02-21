library IncludedSignatureWithAliasOutputTests

include SignatureOutputTests called Overloads

define TestOverload: Overloads.MultipleOverloadTest()
define TestOverloadOneInt: Overloads.MultipleOverloadTest(1)
define TestOverloadOneDecimal: Overloads.MultipleOverloadTest(2.0)
define TestOverloadTwoInts: Overloads.MultipleOverloadTest(1, 2)
define TestOverloadTwoDecimals: Overloads.MultipleOverloadTest(1.0, 2.0)
define TestOverloadOneIntOneDecimal: Overloads.MultipleOverloadTest(1, 2.0)
define TestOverloadOneIntTwoDecimal: Overloads.MultipleOverloadTest(2.0, 1, 2.0)