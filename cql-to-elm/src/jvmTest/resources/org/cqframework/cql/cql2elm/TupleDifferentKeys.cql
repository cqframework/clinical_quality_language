library TupleDifferentKeys

include TranslationTestsCommon version '1.0' called Common

// Could not resolve call to operator Equal with signature (tuple{Foo:System.Any},tuple{Bar:System.Any})
define NotEqual_TupleDifferentKeys: Tuple{ Foo: null } != Tuple{ Bar: null }
// TODO: fix once issue is resolved in translator
define test_NotEqual_TupleDifferentKeys: Common.TestMessage(NotEqual_TupleDifferentKeys, 'NotEqual_TupleDifferentKeys', Common.toString(true), Common.toString(NotEqual_TupleDifferentKeys))
