library pertinencetag

using FHIR version '4.0.1'

define "Observations":
       [Observation]


//@pertinence: pathognomonic
define "Conditions":
        [Condition]

// @pertinence:
define "Medication Requests":
        [MedicationRequest]