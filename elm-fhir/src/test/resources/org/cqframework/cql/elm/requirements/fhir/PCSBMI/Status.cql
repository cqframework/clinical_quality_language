library Status version '1.6.000'


using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

codesystem "ObservationCategoryCodes": 'http://terminology.hl7.org/CodeSystem/observation-category'
codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'

code "laboratory": 'laboratory' from "ObservationCategoryCodes" display 'laboratory'
code "exam": 'exam' from "ObservationCategoryCodes" display 'exam'
code "survey": 'survey' from "ObservationCategoryCodes" display 'survey'
code "vital-signs": 'vital-signs' from "ObservationCategoryCodes" display 'vital-signs'
code "active": 'active' from "ConditionClinicalStatusCodes"

context Patient

//This library contains functions that are based on QDM 5.6 to QICore 4.1.1 March 2023, used for measures authored by NCQA, with notes on intentional differences. The functions in this library have similarities to QICoreCommon (i.e., category constraints) but different in that there are additional constraints such as status or clinical status for each resource.

//Assessment, Performed
define function "Final Survey Observation"(Obs List<Observation>):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "survey"
      )

//Similar but different from QICoreCommon.isSurvey, which does not have status constraints
define fluent function isAssessmentPerformed(Obs List<Observation>):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
          and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "survey"
      )
      
//Diagnosis
define function "Active Condition"(Condition List<Condition>):
  Condition C
    where C.clinicalStatus ~ "active"

//Similar but different from QICoreCommon.isActive, which has recurrence and relapse as additional constraints
define fluent function isActiveOnly(Condition List<Condition>):
  Condition C 
  where C.clinicalStatus ~ "active"

//Device, Order - Personal Use Devices: active and completed only
define function "Completed or Ongoing Device Request"(DeviceRequest List<DeviceRequest>):
  DeviceRequest D
    where D.status in { 'active', 'completed' }
      and D.intent = 'order'

define fluent function isDeviceOrder(DeviceRequest List<DeviceRequest>):
  DeviceRequest D
    where D.status in { 'active', 'completed' }
      and D.intent = 'order'

//Diagnostic Study, Order: active and completed only
//Intervention, Order: active and completed only
//Laboratory Test, Order: active and completed only
define function "Completed or Ongoing Service Request"(ServiceRequest List<ServiceRequest>):
  ServiceRequest S
    where S.status in { 'active', 'completed' }
      and S.intent = 'order'

define fluent function isDiagnosticStudyOrder(ServiceRequest List<ServiceRequest>):
  ServiceRequest S
    where S.status in { 'active', 'completed' }
      and S.intent = 'order'
      
define fluent function isInterventionOrder(ServiceRequest List<ServiceRequest>):
  ServiceRequest S
    where S.status in { 'active', 'completed' }
      and S.intent = 'order'    

define fluent function isLaboratoryTestOrder(ServiceRequest List<ServiceRequest>):
  ServiceRequest S
    where S.status in { 'active', 'completed' }
      and S.intent = 'order'        

//Diagnostic Study, Performed
define function "Final Observation"(Obs List<Observation>):
  Obs O
    where O.status in {'final', 'amended', 'corrected' }

define fluent function isDiagnosticStudyPerformed(Obs List<Observation>):
  Obs O
    where O.status in {'final', 'amended', 'corrected' }
    
//Encounter, Performed
//General usage unless required otherwise by the measure's specific use case (e.g., follow-up encounters)
define function "Finished Encounter"(Enc List<Encounter>):
  Enc E
    where E.status in {'finished', 'arrived', 'triaged', 'in-progress', 'onleave'}
    
define fluent function isEncounterPerformed(Enc List<Encounter>):
  Enc E
    where E.status in {'finished', 'arrived', 'triaged', 'in-progress', 'onleave'}

// //Encounter, Performed: finished and in-progress only
// define function "Finished or Ongoing Encounter"(Enc List<Encounter>):
//   Enc E
//     where E.status in { 'finished', 'in-progress' }

// define fluent function "isEncounterPerformed"(Enc Encounter):
//   Enc.status in { 'finished', 'in-progress' }

//Immunization, Administered: completed only
define function "Completed Immunization"(Immunization List<Immunization>):
  Immunization I
    where I.status ~ 'completed'
    
define fluent function isImmunizationAdministered(Immunization List<Immunization>):
  Immunization I
    where I.status ~ 'completed'

//Intervention, Performed
//Procedure, Performed
define function "Completed Procedure"(Proc List<Procedure>):
  Proc P
    where P.status ~ 'completed'

define fluent function isInterventionPerformed(Proc List<Procedure>):
  Proc P
    where P.status ~ 'completed'

define fluent function isProcedurePerformed(Proc List<Procedure>):
  Proc P
    where P.status ~ 'completed'

//Laboratory Test, Performed
define function "Final Lab Observation"(Obs List<Observation>):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "laboratory"
      )

define fluent function isLaboratoryTestPerformed(Obs List<Observation>):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "laboratory"
      )

//Medication, Active
define function "Active Medication"(MedicationRequest List<MedicationRequest>):
  MedicationRequest M
    where M.status = 'active'
      and M.intent = 'order'

define fluent function isMedicationActive(MedicationRequest List<MedicationRequest>):
  MedicationRequest M
    where M.status = 'active'
      and M.intent = 'order'

//Medication, Dispensed
define function "Dispensed Medication"(Med List<MedicationDispense>):
  Med M
    where M.status in { 'completed', 'in-progress', 'on-hold' }

define fluent function isMedicationDispensed(Med List<MedicationDispense>):
  Med M
    where M.status in { 'completed', 'in-progress', 'on-hold' }

//Medication, Order: active and completed only
define function "Active or Completed Medication Request"(MedicationRequest List<MedicationRequest>):
  MedicationRequest M
    where M.status in { 'active', 'completed' }
      and M.intent = 'order'

define fluent function isMedicationOrder(MedicationRequest List<MedicationRequest>):
  MedicationRequest M
    where M.status in { 'active', 'completed' }
      and M.intent = 'order'

//Physical Exam, Performed
define function "Final Exam Observation"(Obs List<Observation>):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "exam"
      )

define fluent function isPhysicalExamPerformed(Obs List<Observation>):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      and exists ( O.category ObservationCategory
          where ( ObservationCategory ) ~ "exam"
      )

//Observation Vital Signs
define function "BloodPressure"(Obs List<"QICore.observation-bp">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      
define function "BodyHeight"(Obs List<"QICore.observation-bodyheight">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      
define function "BodyWeight"(Obs List<"QICore.observation-bodyweight">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      
define function "BMI"(Obs List<"QICore.observation-bmi">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }

//fluent function versions
define fluent function isObservationBP(Obs List<"QICore.observation-bp">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      
define fluent function isObservationBodyHeight(Obs List<"QICore.observation-bodyheight">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      
define fluent function isObservationBodyWeight(Obs List<"QICore.observation-bodyweight">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }
      
define fluent function isObservationBMI(Obs List<"QICore.observation-bmi">):
  Obs O
    where O.status in { 'final', 'amended', 'corrected' }

//Symptom
define function "Initial or Final Observation"(Obs List<Observation>):
  Obs O
    where O.status in { 'preliminary', 'final', 'amended', 'corrected' }
    
define fluent function isSymptom(Obs List<Observation>):
  Obs O
    where O.status in { 'preliminary', 'final', 'amended', 'corrected' }