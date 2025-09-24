library Query version '1'
using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

codesystem "SNOMED-CT:2017-09": 'http://snomed.info/sct/731000124108'
  version 'http://snomed.info/sct/731000124108/version/201709'

valueset "Encounter Inpatient SNOMEDCT Value Set":
   'https://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.666.7.307|20160929'

code "Venous foot pump, device (physical object)": '442023007' from "SNOMED-CT:2017-09"
code "Right foot": '7769000' from "SNOMED-CT:2017-09"

parameter "Measurement Period" Interval<DateTime>

context Patient

define TestAgeInYearsAt:
  AgeInYearsAt(Patient.deceased)

define "Inpatient Encounter":
  ["Encounter": type in "Encounter Inpatient SNOMEDCT Value Set"] E
    where E.status = 'finished'

// Fails because there is no conversion from a FHIR CodeableConcept to a CQL Code
// Have to use ToConcept to get the code to a concept first
define "Venous Foot Pumps Applied":
  ["DeviceUseStatement": code in "Venous foot pump, device (physical object)"] D
    where D.status = 'completed'
      //and D.bodySite ~ "Right foot"
      and D.bodySite ~ ToConcept("Right foot")

define TestMedicationOrders:
  ["MedicationRequest"] M
	  where M.doNotPerform is true