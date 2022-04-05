/* Context-independent Data Elements. (e.g., the Retrieves, to be used in more than one context.) */
library CommonElements version '1.0.000'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include CommonConcepts version '1.0.000' called Cx

context Patient

/* Potentially "Common" elements */
define "sVitalSigns":
  [Observation: category in 'vital-sign']

define function "Get Active Confirmed Conditions" (value List<Condition>) returns List<Condition>:
  value C
    where C.clinicalStatus in Cx."Active Condition"
      and C.verificationStatus ~ ToConcept(Cx."condition-confirmed")

define function "Get Qualified Observations" (value List<Observation>) returns List<Observation>:
  value O
    where O.status in { 'final', 'amended', 'corrected', 'appended' }
