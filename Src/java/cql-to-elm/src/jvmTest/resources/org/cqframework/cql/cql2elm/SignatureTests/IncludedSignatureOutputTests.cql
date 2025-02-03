library IncludedSignatureOutputTests

include SignatureOutputTests

define TestOverload: SignatureOutputTests.MultipleOverloadTest()
define TestOverloadOneInt: SignatureOutputTests.MultipleOverloadTest(1)
define TestOverloadOneDecimal: SignatureOutputTests.MultipleOverloadTest(2.0)
define TestOverloadTwoInts: SignatureOutputTests.MultipleOverloadTest(1, 2)
define TestOverloadTwoDecimals: SignatureOutputTests.MultipleOverloadTest(1.0, 2.0)
define TestOverloadOneIntOneDecimal: SignatureOutputTests.MultipleOverloadTest(1, 2.0)
define TestOverloadOneIntTwoDecimal: SignatureOutputTests.MultipleOverloadTest(2.0, 1, 2.0)