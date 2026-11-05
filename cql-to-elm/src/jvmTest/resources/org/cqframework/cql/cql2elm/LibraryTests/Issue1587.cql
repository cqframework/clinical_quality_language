// Test for a cast error when a CodeSystemRef does not resolve.
// Previously, was returning an Elm "Null" which can't be cast to a CodeSystemRef.
// https://github.com/cqframework/clinical_quality_language/pull/1587

library Issue1587 

using FHIR version '4.0.1'

include Issue1587Include

// Intentionally referencing a code that does not exist in the included library
code "Test": 'Test' from Issue1587Include.DoesNotExist

// Intentionally referencing a code from a private codesystem
code "PrivateCodes": 'PrivateCodes' from Issue1587Include.PrivateCodes

context Patient