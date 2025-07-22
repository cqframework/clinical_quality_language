// Test for a cast error when a CodeSystemRef does not resolve.
// Previously, was returning an Elm "Null" which can't be cast to a CodeSystemRef.
// https://github.com/cqframework/clinical_quality_language/pull/1587

library Issue1587 

using FHIR version '4.0.1'

include DummyLibrary

code "Test": 'Test' from DummyLibrary.Codes

context Patient