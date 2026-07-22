library TestFhirDataProviderDstu3 version '1.0'

using FHIR version '3.0.0'

valueset "Ambulatory Abuse Potential Opioids": 'http://hl7.org/fhir/ig/opioid-cds/ValueSet/opioids-abused-in-ambulatory-care'

context Patient

define MedReqs: [MedicationRequest]

define testChoiceTypes:
    MedReqs M
        return
            M.dosageInstruction[0].asNeeded

define testDateType:
    AgeInYearsAt(Today()) >= 13

define testFhirObjectEqual:
  FHIR.positiveInt{ value: 1 } = FHIR.positiveInt{ value: 1 }

define testFhirObjectEquivalent:
  FHIR.positiveInt{ value: 1 } ~ FHIR.positiveInt{ value: 1 }

define "Active Ambulatory Opioid Rx":
  [MedicationRequest: "Ambulatory Abuse Potential Opioids"] Rx
    where Rx.status.value = 'active'
      and Rx.authoredOn.value same day or before Today()

define GetProvenance:
  (First([Condition]).contained) C
    where C.Provenance is not null
    return C.Provenance