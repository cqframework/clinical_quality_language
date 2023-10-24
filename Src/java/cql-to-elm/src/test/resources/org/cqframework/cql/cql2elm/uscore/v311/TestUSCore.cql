library TestUSCore

using USCore version '3.1.1'

include FHIRHelpers version '4.0.1'

codesystem "Marital Status Codes": 'http://terminology.hl7.org/CodeSystem/v3-MaritalStatus'
codesystem "CDC Race and Ethnicity Codes": 'urn:oid:2.16.840.1.113883.6.238'

valueset "Marital Status": 'http://hl7.org/fhir/ValueSet/marital-status'

code "Marital Status - Married": 'M' from "Marital Status Codes"
code "American Indian or Alaska Native": '1002-5' from "CDC Race and Ethnicity Codes"
code "Alaska Native": '1735-0' from "CDC Race and Ethnicity Codes"

context Patient

define TestPatient: ["PatientProfile"]
define TestAllergyIntolerance: ["AllergyIntolerance"]
define TestCarePlan: ["CarePlanProfile"]
define TestCareTeam: ["CareTeam"]
define TestCondition: ["Condition"]
define TestDiagnosticReport: ["DiagnosticReportProfileLaboratoryReporting"]
define TestDiagnosticReportNote: ["DiagnosticReportProfileNoteExchange"]
define TestDocumentReference: ["DocumentReferenceProfile"]
define TestEncounter: ["EncounterProfile"]
define TestGoal: ["GoalProfile"]
define TestImmunization: ["ImmunizationProfile"]
define TestImplantableDevice: ["ImplantableDeviceProfile"]
define TestObservation: ["LaboratoryResultObservationProfile"]
define TestLocation: ["Location"]
define TestMedication: ["MedicationProfile"]
define TestMedicationRequest: ["MedicationRequestProfile"]
define TestOrganization: ["OrganizationProfile"]
define TestVitalsPanel: ["observation-vitalspanel"]
define TestRespRate: ["observation-resprate"]
define TestHeartRate: ["observation-heartrate"]
define TestOxygenSat: ["observation-oxygensat"]
define TestBodyTemp: ["observation-bodytemp"]
define TestBodyHeight: ["observation-bodyheight"]
define TestHeadCircum: ["observation-headcircum"]
define TestBodyWeight: ["observation-bodyweight"]
define TestBMI: ["observation-bmi"]
define TestBP: ["observation-bp"]
define TestSmokingStatus: ["SmokingStatusProfile"]
define TestPulseOximetry: ["PulseOximetryProfile"]
define TestPediatricBMIForAge: ["PediatricBMIforAgeObservationProfile"]
define TestPediatricWeightForHeight: ["PediatricWeightForHeightObservationProfile"]
define TestPractitioner: ["PractitionerProfile"]
define TestPractitionerRole: ["PractitionerRoleProfile"]
define TestProcedure: ["ProcedureProfile"]
define TestProvenance: ["Provenance"]

define TestPrimitives:
  Patient P
    where P.gender = 'male'
      and P.active is true
      and P.birthDate before Today()
      and P.maritalStatus in "Marital Status"
      and P.maritalStatus ~ "Marital Status - Married"

define TestPluralPrimitive:
  Patient.name.given[0]

define TestSpecificPluralPrimitive:
  Patient.name[0].given[0]

define TestChoice:
  Patient P
    where P.deceased is false
      or P.deceased before Today()

define TestSlices:
  ["observation-bp"] BP
    where BP.SystolicBP.value < 140 'mm[Hg]'
      and BP.DiastolicBP.value < 90 'mm[Hg]'

define TestSimpleExtensions:
  Patient P
    where P.birthsex = 'M'

define TestComplexExtensions:
  Patient P
    where P.race.ombCategory contains "American Indian or Alaska Native"
      and P.race.detailed contains "Alaska Native"

define TestComplexFHIRHelpers:
  ["PediatricBMIforAgeObservationProfile"] PB
    return PB.VSCat