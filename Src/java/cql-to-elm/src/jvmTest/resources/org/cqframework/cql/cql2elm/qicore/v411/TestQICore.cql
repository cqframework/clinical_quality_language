library TestQICore

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'

codesystem "Marital Status Codes": 'http://terminology.hl7.org/CodeSystem/v3-MaritalStatus'
codesystem "CDC Race and Ethnicity Codes": 'urn:oid:2.16.840.1.113883.6.238'
codesystem "CommunicationCategoryCodeSystem": 'http://terminology.hl7.org/CodeSystem/communication-category'
codesystem "SNOMED-CT": 'http://snomed.info/sct'
codesystem "CVX": 'http://hl7.org/fhir/sid/cvx'
codesystem "LOINC": 'http://loinc.org'
codesystem "TaskCodeSystem": 'http://hl7.org/fhir/CodeSystem/task-code'
codesystem "Hospital Acquired Condition Codes": 'https://www.cms.gov/Medicare/Medicare-Fee-for-Service-Payment/HospitalAcqCond/Coding'

valueset "Marital Status": 'http://hl7.org/fhir/ValueSet/marital-status'
valueset "CommunicationCodes": 'http://hl7.org/fhir/ValueSet/communication-category'
valueset "DeviceCodes": 'http://example.org/fhir/ValueSet/device-codes' // Random set of device codes from 'http://hl7.org/fhir/ValueSet/device-type'
valueset "ImmunizationCodes": 'http://example.org/fhir/ValueSet/immunization-codes' // Random set of immunization codes from 'http://hl7.org/fhir/us/core/ValueSet/us-core-vaccines-cvx'
valueset "MedicationCodes": 'http://example.org/fhir/ValueSet/medication-codes' // Random set of medication codes from 'http://hl7.org/fhir/ValueSet/medication-codes'
valueset "ObservationCodes": 'http://example.org/fhir/ValueSet/observation-codes' // Random set of observation codes from 'http://hl7.org/fhir/ValueSet/observation-codes'
valueset "ProcedureCodes": 'http://example.org/fhir/ValueSet/procedure-codes' // Random set of procedure codes from 'http://hl7.org/fhir/us/core/ValueSet/us-core-procedure-code'
valueset "TaskCodes": 'http://hl7.org/fhir/ValueSet/task-code'
valueset "NegationReasonCodes": 'http://hl7.org/fhir/us/qicore/ValueSet/qicore-negation-reason'

code "Marital Status - Married": 'M' from "Marital Status Codes"
code "American Indian or Alaska Native": '1002-5' from "CDC Race and Ethnicity Codes"
code "Alaska Native": '1735-0' from "CDC Race and Ethnicity Codes"
code "CommunicationCode": 'alert' from "CommunicationCategoryCodeSystem" display 'Alert'
code "DeviceCode": '156009' from "SNOMED-CT" display 'Spine board'
code "ImmunizationCode": '01' from "CVX" display 'diphtheria, tetanus toxoids and pertussis vaccine'
code "MedicationCode": '26100' from "SNOMED-CT" display 'Codeine phosphate'
code "ObservationCode": '1-8' from "LOINC" display 'Acyclovir [Susceptibility]'
code "ProcedureCode": '7138802' from "SNOMED-CT" display 'Procedure'
code "TaskCode": 'approve' from "TaskCodeSystem" display 'Activate/approve the focal resource'
code "Y": 'Y' from "Hospital Acquired Condition Codes" display 'Yes'

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
define TestDiagnosticReportLab: ["DiagnosticReportLab"]
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
define TestLocation: ["Location"]
define TestMedication: ["Medication"]
define TestMedicationAdministration: ["MedicationAdministration"]
define TestMedicationAdministrationNotDone: ["MedicationAdministrationNotDone"]
define TestMedicationDispense: ["MedicationDispense"]
define TestMedicationDispenseNotDone: ["MedicationDispenseNotDone"]
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
define TestTaskNotDone: ["TaskNotDone"]
define TestUSCoreImplantableDevice: ["USCoreImplantableDeviceProfile"]
define TestUSCoreLaboratoryResult: ["USCoreLaboratoryResultObservationProfile"]
define TestUSCoreSmokingStatus: ["USCoreSmokingStatusProfile"]
define TestUSCorePulseOximetry: ["USCorePulseOximetryProfile"]
define TestUSCorePediatricBMIForAge: ["USCorePediatricBMIforAgeObservationProfile"]
define TestUSCorePediatricWeightForHeight: ["USCorePediatricWeightForHeightObservationProfile"]

define TestAge:
  AgeInYears()

define TestAgeAt:
  AgeInYearsAt(@2022-10-14)

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

define TestSimpleExtensions:
  Patient P
    where P.birthsex = 'M'

define TestComplexExtensions:
  Patient P
    where P.race.ombCategory contains "American Indian or Alaska Native"
      and P.race.detailed contains "Alaska Native"

// Validate Encounter.diagnosis cardinality
define TestEncounterDiagnosisCardinality:
    exists (First([Encounter]).diagnosis)

// Validate diagnosisPresentOnAdmission
define TestEncounterDiagnosisPresentOnAdmission:
    exists ((First([Encounter]).diagnosis) D
      where D.diagnosisPresentOnAdmission ~ "Y"
    )

// Validate ProcedureNotDone elements
define TestProcedureNotDoneElements:
    [ProcedureNotDone] P
      where P.recorded on or before day of Today()
        and exists (P.identifier)
        and P.status = 'not-done'
        and P.statusReason is not null
        and P.category is not null
        and P.code is not null
        and P.performed is null

// Validate NotDone...

// CommunicationNotDone
define TestSpecificCommunicationNotDone:
    ["CommunicationNotDone": CommunicationCode]

// TODO: Support this case, need to introduce a terminology contains operator
//define TestSpecificCommunicationNotDoneActual:
//    ["CommunicationNotDone": reasonCode ~ CommunicationCode]
//      union ["CommunicationNotDone": reasonCode contains CommunicationCode]

define TestSpecificCommunicationNotDoneExplicit:
    ["CommunicationNotDone"] C
      where C.reasonCode ~ CommunicationCode
        or CommunicationCode in C.reasonCode

// NOTE: Consider using topic rather than reasonCode as the negation focus?
//define TestSpecificCommunicationNotDoneExplicit:
//    [Communication] C
//      where (C.reasonCode ~ CommunicationCode
//          or CommunicationCode in ValueSet { id: C.reasonCode.extension("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneValueSet").value as String }
//        )
//        and C.status = 'not-done'
//        and C.recorded on or before day of Today()
//        and C.statusReason in NegationReasonCodes

define TestGeneralCommunicationNotDone:
    ["CommunicationNotDone": CommunicationCodes]

//define TestGeneralCommunicationNotDoneActual:
//    ["CommunicationNotDone": reasonCode in CommunicationCodes]
//      union ["CommunicationNotDone": reasonCode ~ CommunicationCodes]

define TestGeneralCommunicationNotDoneExplicit:
    ["CommunicationNotDone"] C
      where C.reasonCode in CommunicationCodes
        or C.reasonCode ~ CommunicationCodes

//define TestGeneralCommunicationNotDoneExplicit:
//    [Communication] C
//      where (C.reasonCode in CommunicationCodes
//          or CommunicationCodes ~ ValueSet { id: C.reasonCode.extension("http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneValueSet").value as String }
//        )
//        and C.status = 'not-done'
//        and C.recorded on or before day of Today()
//        and C.statusReason in NegationReasonCodes

// DeviceNotRequested
define TestSpecificDeviceNotRequested:
    ["DeviceNotRequested": DeviceCode]

// TODO: Support this use case
//define TestSpecificDeviceNotRequestedActual:
//    ["DeviceNotRequested": code ~ DeviceCode]
//      union ["DeviceNotRequested": code contains DeviceCode]

define TestSpecificDeviceNotRequestedExplicit:
    [DeviceNotRequested] D
      where D.code ~ DeviceCode
        or DeviceCode in D.code

define TestGeneralDeviceNotRequested:
    ["DeviceNotRequested": DeviceCodes]

define TestGeneralDeviceNotRequestedCode:
    ["DeviceNotRequested": code in DeviceCodes]

define TestGeneralDeviceNotRequestedValueSet:
    ["DeviceNotRequested": code ~ DeviceCodes]

define TestGeneralDeviceNotRequestedActual:
    ["DeviceNotRequested": code in DeviceCodes]
      union ["DeviceNotRequested": code ~ DeviceCodes]

define TestGeneralDeviceNotRequestedExplicit:
    ["DeviceNotRequested"] D
      where D.code in DeviceCodes
        or D.code ~ DeviceCodes

define TestGeneralDeviceNotRequestedCodeExplicit:
    ["DeviceNotRequested"] D
      where D.code in DeviceCodes

define TestGeneralDeviceNotRequestedValueSetExplicit:
    ["DeviceNotRequested"] D
      where D.code ~ DeviceCodes


// ImmunizationNotDone
define TestSpecificImmunizationNotDone:
    ["ImmunizationNotDone": ImmunizationCode]

// TODO: Support this use case
//define TestSpecificImmunizationNotDoneActual:
//    ["ImmunizationNotDone": vaccineCode ~ ImmunizationCode]
//      union ["ImmunizationNotDone": vaccineCode contains ImmunizationCode]

define TestSpecificImmunizationNotDoneExplicit:
    ["ImmunizationNotDone"] I
      where I.vaccineCode ~ ImmunizationCode
        or ImmunizationCode in I.vaccineCode

define TestGeneralImmunizationNotDone:
    ["ImmunizationNotDone": ImmunizationCodes]

define TestGeneralImmunizationNotDoneActual:
    ["ImmunizationNotDone": vaccineCode in ImmunizationCodes]
      union ["ImmunizationNotDone": vaccineCode ~ ImmunizationCodes]

define TestGeneralImmunizationNotDoneExplicit:
    ["ImmunizationNotDone"] I
      where I.vaccineCode in ImmunizationCodes
        or I.vaccineCode ~ ImmunizationCodes

// MedicationAdministrationNotDone
define TestSpecificMedicationAdministrationNotDone:
    ["MedicationAdministrationNotDone": MedicationCode]

// TODO: Support this use case
//define TestSpecificMedicationAdministrationNotDoneActual:
//    ["MedicationAdministrationNotDone": medication ~ MedicationCode]
//      union ["MedicationAdministrationNotDone": medication contains MedicationCode]

define TestSpecificMedicationAdministrationNotDoneExplicit:
    ["MedicationAdministrationNotDone"] I
      where I.medication ~ MedicationCode
        or MedicationCode in I.medication

define TestGeneralMedicationAdministrationNotDone:
    ["MedicationAdministrationNotDone": MedicationCodes]

define TestGeneralMedicationAdministrationNotDoneActual:
    ["MedicationAdministrationNotDone": medication in MedicationCodes]
      union ["MedicationAdministrationNotDone": medication ~ MedicationCodes]

define TestGeneralMedicationAdministrationNotDoneExplicit:
    ["MedicationAdministrationNotDone"] I
      where I.medication in MedicationCodes
        or I.medication ~ MedicationCodes

// MedicationDispenseNotDone
define TestSpecificMedicationDispenseNotDone:
    ["MedicationDispenseNotDone": MedicationCode]

// TODO: Support this use case
//define TestSpecificMedicationDispenseNotDoneActual:
//    ["MedicationDispenseNotDone": medication ~ MedicationCode]
//      union ["MedicationDispenseNotDone": medication contains MedicationCode]

define TestSpecificMedicationDispenseNotDoneExplicit:
    ["MedicationDispenseNotDone"] I
      where I.medication ~ MedicationCode
        or MedicationCode in I.medication

define TestGeneralMedicationDispenseNotDone:
    ["MedicationDispenseNotDone": MedicationCodes]

define TestGeneralMedicationDispenseNotDoneActual:
    ["MedicationDispenseNotDone": medication in MedicationCodes]
      union ["MedicationDispenseNotDone": medication ~ MedicationCodes]

define TestGeneralMedicationDispenseNotDoneExplicit:
    ["MedicationDispenseNotDone"] I
      where I.medication in MedicationCodes
        or I.medication ~ MedicationCodes

// MedicationNotRequested
define TestSpecificMedicationNotRequested:
    ["MedicationNotRequested": MedicationCode]

// TODO: Support this use case
//define TestSpecificMedicationNotRequestedActual:
//    ["MedicationNotRequested": medication ~ MedicationCode]
//      union ["MedicationNotRequested": medication contains MedicationCode]

define TestSpecificMedicationNotRequestedExplicit:
    ["MedicationNotRequested"] I
      where I.medication ~ MedicationCode
        or MedicationCode in I.medication

define TestGeneralMedicationNotRequested:
    ["MedicationNotRequested": MedicationCodes]

define TestGeneralMedicationNotRequestedActual:
    ["MedicationNotRequested": medication in MedicationCodes]
      union ["MedicationNotRequested": medication ~ MedicationCodes]

define TestGeneralMedicationNotRequestedExplicit:
    ["MedicationNotRequested"] I
      where I.medication in MedicationCodes
        or I.medication ~ MedicationCodes

// ObservationNotDone
define TestSpecificObservationNotDone:
    ["ObservationNotDone": ObservationCode]

// TODO: Support this use case
//define TestSpecificObservationNotDoneActual:
//    ["ObservationNotDone": code ~ ObservationCode]
//      union ["ObservationNotDone": code contains ObservationCode]

define TestSpecificObservationNotDoneExplicit:
    ["ObservationNotDone"] I
      where I.code ~ ObservationCode
        or ObservationCode in I.code

define TestGeneralObservationNotDone:
    ["ObservationNotDone": ObservationCodes]

define TestGeneralObservationNotDoneActual:
    ["ObservationNotDone": code in ObservationCodes]
      union ["ObservationNotDone": code ~ ObservationCodes]

define TestGeneralObservationNotDoneExplicit:
    ["ObservationNotDone"] I
      where I.code in ObservationCodes
        or I.code ~ ObservationCodes

// ProcedureNotDone
define TestSpecificProcedureNotDone:
    ["ProcedureNotDone": ProcedureCode]

// TODO: Support this use case
//define TestSpecificProcedureNotDoneActual:
//    ["ProcedureNotDone": code ~ ProcedureCode]
//      union ["ProcedureNotDone": code contains ProcedureCode]

define TestSpecificProcedureNotDoneExplicit:
    ["ProcedureNotDone"] I
      where I.code ~ ProcedureCode
        or ProcedureCode in I.code

define TestGeneralProcedureNotDone:
    ["ProcedureNotDone": ProcedureCodes]

define TestGeneralProcedureNotDoneActual:
    ["ProcedureNotDone": code in ProcedureCodes]
      union ["ProcedureNotDone": code ~ ProcedureCodes]

define TestGeneralProcedureNotDoneExplicit:
    ["ProcedureNotDone"] I
      where I.code in ProcedureCodes
        or I.code ~ ProcedureCodes

// ServiceNotRequested
define TestSpecificServiceNotRequested:
    ["ServiceNotRequested": ProcedureCode]

// TODO: Support this use case
//define TestSpecificServiceNotRequestedActual:
//    ["ServiceNotRequested": code ~ ProcedureCode]
//      union ["ServiceNotRequested": code contains ProcedureCode]

define TestSpecificServiceNotRequestedExplicit:
    ["ServiceNotRequested"] I
      where I.code ~ ProcedureCode
        or ProcedureCode in I.code

define TestGeneralServiceNotRequested:
    ["ServiceNotRequested": ProcedureCodes]

define TestGeneralServiceNotRequestedActual:
    ["ServiceNotRequested": code in ProcedureCodes]
      union ["ServiceNotRequested": code ~ ProcedureCodes]

define TestGeneralServiceNotRequestedExplicit:
    ["ServiceNotRequested"] I
      where I.code in ProcedureCodes
        or I.code ~ ProcedureCodes

// TaskNotDone
define TestSpecificTaskNotDone:
    ["TaskNotDone": TaskCode]

// TODO: Support this use case
//define TestSpecificTaskNotDoneActual:
//    ["TaskNotDone": code ~ TaskCode]
//      union ["TaskNotDone": code contains TaskCode]

define TestSpecificTaskNotDoneExplicit:
    ["TaskNotDone"] I
      where I.code ~ TaskCode
        or TaskCode in I.code

define TestGeneralTaskNotDone:
    ["TaskNotDone": TaskCodes]

define TestGeneralTaskNotDoneActual:
    ["TaskNotDone": code in TaskCodes]
      union ["TaskNotDone": code ~ TaskCodes]

define TestGeneralTaskNotDoneExplicit:
    ["TaskNotDone"] I
      where I.code in TaskCodes
        or I.code ~ TaskCodes
