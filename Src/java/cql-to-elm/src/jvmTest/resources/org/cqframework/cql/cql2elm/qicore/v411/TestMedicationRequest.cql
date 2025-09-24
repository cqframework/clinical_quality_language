library TestMedicationRequest

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'

valueset "Antithrombotic Therapy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1110.62'

context Patient

define "Antithrombotic Therapy at Discharge":
  ["MedicationRequest": medication in "Antithrombotic Therapy"] Antithrombotic

define "Antithrombotic Therapy at Discharge (2)":
  ["MedicationRequest": "Antithrombotic Therapy"]
