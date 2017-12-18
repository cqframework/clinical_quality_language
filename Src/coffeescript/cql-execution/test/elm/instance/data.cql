// @Test: Instance
using QUICK
define QuantityA: Quantity {
  unit: 'a',
  value: 12
}

define CodeA: Code {
  system: 'http://loinc.org',
  code: '12345',
  version: '1',
  display: 'Test Code'
}

define ConceptA: Concept {
  codes: { CodeA },
  display: 'Test Concept'
}

define Med : Medication {
  name: 'Best Med Ever',
  isBrand: false
}

define val: QuantityA.value



