library GeometricMean_Zero

include TranslationTestsCommon version '1.0' called Common

// TODO: possible issue in translator causing GeometricMean_Zero = 0 to fail (Integer not being implicitly promoted to Decimal)
define GeometricMean_Zero: GeometricMean({2.0, 8.0, 0})

define test_GeometricMean_Zero: Common.TestMessage(GeometricMean_Zero = 0.0, 'GeometricMean_Zero', Common.toString(0.0), Common.toString(GeometricMean_Zero))
