/*
NOTE: Preliminary QICore 6.0.0 uplift to support connectathon 38 testing
*/
library Status version '2.0.000'

using QICore version '6.0.0'

include FHIRHelpers version '4.0.1' called FHIRHelpers

codesystem "ObservationCategoryCodes": 'http://terminology.hl7.org/CodeSystem/observation-category'
codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'

code "laboratory": 'laboratory' from "ObservationCategoryCodes" display 'laboratory'
code "exam": 'exam' from "ObservationCategoryCodes" display 'exam'
code "survey": 'survey' from "ObservationCategoryCodes" display 'survey'
code "vital-signs": 'vital-signs' from "ObservationCategoryCodes" display 'vital-signs'
code "clinicalStatusActive": 'active' from "ConditionClinicalStatusCodes"

context Patient
//Outdated: "This library contains functions that are based on QDM 5.6 to QICore 4.1.1 March 2023 (https://github.com/cqframework/CQL-Formatting-and-Usage-Wiki/wiki/Authoring-Patterns---QICore-v4.1.1). The functions may appear similar to some QICoreCommon functions but different in that they have constraints that are relevant for measures authored by NCQA."

//Assessment, Performed
define fluent function isAssessmentPerformed(Obs List<Choice<"SimpleObservation", "ObservationScreeningAssessment">>):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
          and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "survey"
      )

//Encounter, Performed
define fluent function isEncounterPerformed(Enc List<Encounter>):
  Enc E
    where E.status in {'finished', 'arrived', 'triaged', 'in-progress', 'onleave'}

//Intervention, Performed
define fluent function isInterventionPerformed(Proc List<Procedure>):
  Proc P
    where P.status ~ 'completed'

//Intervention, Order: active and completed only
define fluent function isInterventionOrder(ServiceRequest List<ServiceRequest>):
  ServiceRequest S
    where S.status in { 'active', 'completed' }
      and S.intent = 'order'

/*
//Diagnosis
define function "Active Condition"(Condition List<Choice<"ConditionProblemsHealthConcerns", "ConditionEncounterDiagnosis">>):
  Condition C
    where C.clinicalStatus ~ "clinicalStatusActive"

//Similar but different from QICoreCommon.isActive, which has recurrence and relapse as additional constraints
define fluent function isActiveOnly(Condition List<Choice<"ConditionProblemsHealthConcerns", "ConditionEncounterDiagnosis">>):
  Condition C
    where C.clinicalStatus ~ "clinicalStatusActive"

//Device, Order - Personal Use Devices: active and completed only
define function "Completed or Ongoing Device Request"(DeviceRequest List<DeviceRequest>):
  DeviceRequest.isDeviceOrder()

define fluent function isDeviceOrder(DeviceRequest List<DeviceRequest>):
  DeviceRequest D
    where D.status in { 'active', 'completed' }
      and D.intent = 'order'

//Diagnostic Study, Order: active and completed only
define function "Completed or Ongoing Service Request"(ServiceRequest List<ServiceRequest>):
  ServiceRequest.isDiagnosticStudyOrder()

define fluent function isDiagnosticStudyOrder(ServiceRequest List<ServiceRequest>):
  ServiceRequest S
    where S.status in { 'active', 'completed' }
      and S.intent = 'order'

//Laboratory Test, Order: active and completed only
define fluent function isLaboratoryTestOrder(ServiceRequest List<ServiceRequest>):
  ServiceRequest S
    where S.status in { 'active', 'completed' }
      and S.intent = 'order'

//Diagnostic Study, Performed
define function "Final Observation"(Obs List<Choice<"SimpleObservation",
"NonPatientObservation",
"LaboratoryResultObservation",
"ObservationClinicalResult",
"ObservationScreeningAssessment">>):
  Obs.isDiagnosticStudyPerformed()

define fluent function isDiagnosticStudyPerformed(Obs List<Choice<"SimpleObservation",
"LaboratoryResultObservation",
"ObservationClinicalResult",
"ObservationScreeningAssessment">>):
  Obs O
    where O.status in {'final', 'amended', 'corrected' }

//Encounter, Performed
//General usage unless required otherwise by measure intent (e.g., follow-up encounters)
define function "Finished Encounter"(Enc List<Encounter>):
  isEncounterPerformed()

//Immunization, Administered: completed only
define function "Completed Immunization"(Immunization List<Immunization>):
  isImmunizationAdministered()

define fluent function isImmunizationAdministered(Immunization List<Immunization>):
  Immunization I
    where I.status ~ 'completed'


//Procedure, Performed
define function "Completed Procedure"(Proc List<Procedure>):
  isProcedurePerformed()

define fluent function isProcedurePerformed(Proc List<Procedure>):
  Proc P
    where P.status ~ 'completed'

//Laboratory Test, Performed
define fluent function isLaboratoryTestPerformed(Obs List<"LaboratoryResultObservation">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "laboratory"
      )

//Medication, Active
define function "Active Medication"(MedicationRequest List<MedicationRequest>):
  isMedicationActive()

define fluent function isMedicationActive(MedicationRequest List<MedicationRequest>):
  MedicationRequest M
    where M.status = 'active'
      and M.intent = 'order'

//Medication, Dispensed
define function "Dispensed Medication"(Med List<MedicationDispense>):
  isMedicationDispensed()

define fluent function isMedicationDispensed(Med List<MedicationDispense>):
  Med M
    where M.status in { 'completed', 'in-progress', 'on-hold' }

//Medication, Order: active and completed only
define function "Active or Completed Medication Request"(MedicationRequest List<MedicationRequest>):
  isMedicationOrder()

define fluent function isMedicationOrder(MedicationRequest List<MedicationRequest>):
  MedicationRequest M
    where M.status in { 'active', 'completed' }
      and M.intent = 'order'

//Physical Exam, Performed
define fluent function isPhysicalExamPerformed(Obs List<Choice<"SimpleObservation", "ObservationClinicalResult">>):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "exam"
      )

//Observation Vital Signs
define fluent function isObservationBP(Obs List<"USCoreBloodPressureProfile">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }

define fluent function isObservationBodyHeight(Obs List<"USCoreBodyHeightProfile">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }

define fluent function isObservationBodyWeight(Obs List<"USCoreBodyWeightProfile">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }

define fluent function isObservationBMI(Obs List<"USCoreBMIProfile">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }

//Symptom
define function "Initial or Final Observation"(Obs List<Choice<"SimpleObservation",
"LaboratoryResultObservation",
"ObservationClinicalResult",
"ObservationScreeningAssessment",
"NonPatientObservation">>):
  Obs O
    where O.status in { 'preliminary', 'final', 'amended', 'corrected' }

define fluent function isSymptom(Obs List<Choice<"SimpleObservation",
"ObservationClinicalResult",
"ObservationScreeningAssessment">>):
  Obs O
    where O.status in { 'preliminary', 'final', 'amended', 'corrected' }
*/