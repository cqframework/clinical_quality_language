library Retrieve1 version '1.0'
using FHIR version '3.0.0'
include FHIRHelpers version '3.0.0' called FHIRHelpers
context Patient

define Money: FHIR.Money {
  value: FHIR.decimal { value: 1.0 },
  code: FHIR.code { value: '$' },
  system: FHIR.uri { value: 'urn:iso:std:iso:4217' }
}

// This requires the FHIR.uri selector
define MoneyInvariant:
  Money.select((code."exists"() or value.empty()) and (system.empty() or system = 'urn:iso:std:iso:4217'))
