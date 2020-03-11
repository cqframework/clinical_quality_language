library TestQICore

using QICore version '4.0.0'

codesystem "Marital Status Codes": 'http://terminology.hl7.org/CodeSystem/v3-MaritalStatus'
codesystem "CDC Race and Ethnicity Codes": 'urn:oid:2.16.840.1.113883.6.238'

valueset "Marital Status": 'http://hl7.org/fhir/ValueSet/marital-status'

code "Marital Status - Married": 'M' from "Marital Status Codes"
code "American Indian or Alaska Native": '1002-5' from "CDC Race and Ethnicity Codes"
code "Alaska Native": '1735-0' from "CDC Race and Ethnicity Codes"

context Patient

define TestAdverseEvent: ["AdverseEvent"]
define TestAllergyIntolerance: ["AllergyIntolerance"]
define TestBodyStructure: ["BodyStructure"]
define TestCarePlan: ["CarePlan"]
define TestCareTeam: ["CareTeam"]
define TestClaim: ["Claim"]
define TestCommunication: ["Communication"]
define TestCommunicationNotDone: ["CommunicationNotDone"]
define TestCommunicationRequest: ["CommunicationRequest"]
define TestCondition: ["Condition"]
define TestCoverage: ["Coverage"]
define TestDevice: ["Device"]
define TestDeviceNotRequested: ["DeviceNotRequested"]
define TestDeviceRequest: ["DeviceRequest"]
define TestDeviceUseStatement: ["DeviceUseStatement"]
define TestDiagnosticReport: ["DiagnosticReportLab"]
define TestDiagnosticReportNote: ["DiagnosticReportNote"]
define TestEncounter: ["Encounter"]
define TestFamilyMemberHistory: ["FamilyMemberHistory"]
define TestFlag: ["Flag"]
define TestGoal: ["Goal"]
define TestImagingStudy: ["ImagingStudy"]
define TestImmunization: ["Immunization"]
define TestImmunizationEvaluation: ["ImmunizationEvaluation"]
define TestImmunizationNotDone: ["ImmunizationNotDone"]
define TestImmunizationRecommendation: ["ImmunizationRecommendation"]
define TestImplantableDevice: ["USCoreImplantableDeviceProfile"]
define TestLaboratoryResult: ["USCoreLaboratoryResultObservationProfile"]
define TestLocation: ["Location"]
define TestMedication: ["Medication"]
define TestMedicationAdministration: ["MedicationAdministration"]
define TestMedicationAdministrationNotDone: ["MedicationAdministrationNotDone"]
define TestMedicationDispense: ["MedicationDispense"]
define TestMedicationNotDispensed: ["MedicationDispenseNotDone"]
define TestMedicationNotRequested: ["MedicationNotRequested"]
define TestMedicationRequest: ["MedicationRequest"]
define TestMedicationStatement: ["MedicationStatement"]
define TestObservation: ["Observation"]
define TestObservationNotDone: ["ObservationNotDone"]
define TestOrganization: ["Organization"]
define TestPatient: ["Patient"]
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
define TestSmokingStatus: ["USCoreSmokingStatusProfile"]
define TestPulseOximetry: ["USCorePulseOximetryProfile"]
define TestPediatricBMIForAge: ["USCorePediatricBMIforAgeObservationProfile"]
define TestPediatricWeightForHeight: ["USCorePediatricWeightForHeightObservationProfile"]
define TestPractitioner: ["Practitioner"]
define TestPractitionerRole: ["PractitionerRole"]
define TestProcedure: ["Procedure"]
define TestProcedureNotDone: ["ProcedureNotDone"]
define TestRelatedPerson: ["RelatedPerson"]
define TestServiceNotRequested: ["ServiceNotRequested"]
define TestServiceRequest: ["ServiceRequest"]
define TestSpecimen: ["Specimen"]
define TestSubstsance: ["Substance"]
define TestTask: ["Task"]

define TestPrimitives:
  Patient P
    where P.gender = 'male'
      and P.active is true
      and P.birthDate before Today()
      and P.maritalStatus in "Marital Status"
      and P.maritalStatus ~ "Marital Status - Married"

define TestChoice:
  Patient P
    where P.deceased is false
      or P.deceased before Today()

define TestSlices:
  ["observation-bp"] BP
    where BP.SystolicBP.value < 140 'mm[Hg]'
      and BP.DiastolicBP.value < 90 'mm[Hg]'

// TODO: This should not need the .value here
define TestSimpleExtensions:
  Patient P
    where P.birthsex.value = 'M'

define TestComplexExtensions:
  Patient P
    where P.race.ombCategory contains "American Indian or Alaska Native"
      and P.race.detailed contains "Alaska Native"



