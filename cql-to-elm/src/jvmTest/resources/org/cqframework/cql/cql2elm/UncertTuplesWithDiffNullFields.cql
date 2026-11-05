library UncertTuplesWithDiffNullFields

include TranslationTestsCommon version '1.0' called Common

define NotEqual_UncertTuplesWithDiffNullFields: Tuple{a: 'Hello', b: null} != Tuple{a: 'Hello', c: null}
// TODO: fix once issue is resolved in translator
define test_NotEqual_UncertTuplesWithDiffNullFields: Common.TestMessage(NotEqual_UncertTuplesWithDiffNullFields, 'NotEqual_UncertTuplesWithDiffNullFields', Common.toString(true), Common.toString(NotEqual_UncertTuplesWithDiffNullFields))
