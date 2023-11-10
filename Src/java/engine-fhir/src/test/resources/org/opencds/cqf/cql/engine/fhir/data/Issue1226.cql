library Issue1226

using USCore version '3.1.1'

include FHIRHelpers version '4.0.1'

context Patient

define "All Medication Requests":
  ["MedicationRequestProfile"]

define "Most Recent Medication Request":
  First("All Medication Requests")

define "Most Recent Medication Request reference":
  "Most Recent Medication Request".medication.reference