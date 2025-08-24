library TestUSCore

using USCore version '6.1.0'

include FHIRHelpers version '4.0.1'

codesystem "Marital Status Codes": 'http://terminology.hl7.org/CodeSystem/v3-MaritalStatus'
codesystem "CDC Race and Ethnicity Codes": 'urn:oid:2.16.840.1.113883.6.238'

valueset "Marital Status": 'http://hl7.org/fhir/ValueSet/marital-status'

code "Marital Status - Married": 'M' from "Marital Status Codes"
code "American Indian or Alaska Native": '1002-5' from "CDC Race and Ethnicity Codes"
code "Alaska Native": '1735-0' from "CDC Race and Ethnicity Codes"

context Patient

define TestAllergyIntolerance: ["AllergyIntolerance"]
define TestCarePlan: ["CarePlanProfile"]
define TestCareTeam: ["CareTeam"]
define TestConditionEncounterDiagnosisProfile: ["ConditionEncounterDiagnosisProfile"]
define TestConditionProblemsHealthConcernsProfile: ["ConditionProblemsHealthConcernsProfile"]
define TestCoverage: ["CoverageProfile"]
define TestImplantableDevice: ["ImplantableDeviceProfile"]
define TestDiagnosticReport: ["DiagnosticReportProfileLaboratoryReporting"]
define TestDiagnosticReportNote: ["DiagnosticReportProfileNoteExchange"]
define TestDocumentReference: ["DocumentReferenceProfile"]
define TestEncounter: ["EncounterProfile"]
define TestGoal: ["GoalProfile"]
define TestImmunization: ["ImmunizationProfile"]
define TestLocation: ["Location"]
define TestMedication: ["MedicationProfile"]
define TestMedicationDispense: ["MedicationDispenseProfile"]
define TestMedicationRequest: ["MedicationRequestProfile"]
define TestObservationClinicalResultProfile: ["ObservationClinicalResultProfile"]
define TestLaboratoryResultObservationProfile: ["LaboratoryResultObservationProfile"]
define TestObservationOccupationProfile: ["ObservationOccupationProfile"]
define TestObservationPregnancyIntentProfile: ["ObservationPregnancyIntentProfile"]
define TestObservationPregnancyStatusProfile: ["ObservationPregnancyStatusProfile"]
define TestObservationScreeningAssessmentProfile: ["ObservationScreeningAssessmentProfile"]
define TestObservationSexualOrientationProfile: ["ObservationSexualOrientationProfile"]
define TestSimpleObservationProfile: ["SimpleObservationProfile"]
define TestSmokingStatus: ["SmokingStatusProfile"]
define TestVitalSignsProfile: ["VitalSignsProfile"]
define TestPediatricHeadOccipitalFrontalCircumferencePercentileProfile: ["PediatricHeadOccipitalFrontalCircumferencePercentileProfile"]
define TestPediatricBMIForAge: ["PediatricBMIforAgeObservationProfile"]
define TestPediatricWeightForHeight: ["PediatricWeightForHeightObservationProfile"]
define TestBloodPressureProfile: ["BloodPressureProfile"]
define TestBMIProfile: ["BMIProfile"]
define TestBodyHeightProfile: ["BodyHeightProfile"]
define TestBodyTemperatureProfile: ["BodyTemperatureProfile"]
define TestBodyWeightProfile: ["BodyWeightProfile"]
define TestHeadCircumferenceProfile: ["HeadCircumferenceProfile"]
define TestHeartRateProfile: ["HeartRateProfile"]
define TestPulseOximetryProfile: ["PulseOximetryProfile"]
define TestRespiratoryRateProfile: ["RespiratoryRateProfile"]
define TestOrganization: ["OrganizationProfile"]
define TestPatient: ["PatientProfile"]
define TestPractitioner: ["PractitionerProfile"]
define TestPractitionerRole: ["PractitionerRoleProfile"]
define TestProcedure: ["ProcedureProfile"]
define TestProvenance: ["Provenance"]
define TestQuestionnaireResponseProfile: ["QuestionnaireResponseProfile"]
define TestRelatedPersonProfile: ["RelatedPersonProfile"]
define TestServiceRequestProfile: ["ServiceRequestProfile"]
define TestSpecimenProfile: ["SpecimenProfile"]

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
  ["BloodPressureProfile"] BP
    where BP.systolic.value < 140 'mm[Hg]'
      and BP.diastolic.value < 90 'mm[Hg]'

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