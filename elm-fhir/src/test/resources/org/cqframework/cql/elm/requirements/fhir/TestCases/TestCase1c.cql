library TestCase1c

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Aspirin": 'http://example.org/fhir/ValueSet/aspirin'

context Patient

/*
1c: Referenced data requirement
DataRequirement
type: Medication
codeFilter: { path: code, valueset: http://example.org/fhir/ValueSet/aspirin }
 */
define TestMedicationRequirement:
  [Medication]

// If only TestReferencedDataRequirement is referenced, the data requirement should only be [Medication: code]
define TestReferencedDataRequirement:
  TestMedicationRequirement R
    where R.code in "Aspirin"

