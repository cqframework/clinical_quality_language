library NCQA_AdvancedIllnessandFrailty version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include NCQA_FHIRBase version '1.0.0' called FHIRBase
include NCQA_Status version '1.0.0' called Status
include NCQA_Encounter version '1.0.0' called Encounters
include NCQA_Claims version '1.0.0' called Claims

valueset "Acute Inpatient": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1810'
valueset "Advanced Illness": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1465'
valueset "Dementia Medications": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1729'
valueset "ED": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1086'
valueset "Frailty Device": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1530'
valueset "Frailty Diagnosis": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1531'
valueset "Frailty Encounter": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1532'
valueset "Frailty Symptom": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1533'
valueset "Nonacute Inpatient": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1189'
valueset "Observation": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1191'
valueset "Online Assessments": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1446'
valueset "Outpatient": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1202'
valueset "Telephone Visits": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1246'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Advanced Illness and Frailty Exclusion Including Over Age 80":
  ( AgeInYearsAt(date from 
      end of "Measurement Period"
    )in Interval[66, 80]
      and "Has Criteria Indicating Frailty"
      and ( "Two Outpatient Visits with Advanced Illness on Different Dates of Service"
          or "Acute Inpatient Encounter with Advanced Illness"
          or "Acute Inpatient Discharge with Advanced Illness"
          or "Dementia Medications In Year Before or During Measurement Period"
      )
  )
    or ( AgeInYearsAt(date from 
        end of "Measurement Period"
      )>= 81
        and "Has Criteria Indicating Frailty"
    )

define "Advanced Illness and Frailty Exclusion Not Including Over Age 80":
  AgeInYearsAt(date from 
    end of "Measurement Period"
  )>= 66
    and "Has Criteria Indicating Frailty"
    and ( "Two Outpatient Visits with Advanced Illness on Different Dates of Service"
        or "Acute Inpatient Encounter with Advanced Illness"
        or "Acute Inpatient Discharge with Advanced Illness"
        or "Dementia Medications In Year Before or During Measurement Period"
    )

define "Has Criteria Indicating Frailty":
  exists ( [Observation: "Frailty Device"] FrailtyDeviceApplied
      where FHIRBase."Normalize Interval" ( FrailtyDeviceApplied.effective ) overlaps "Measurement Period"
  )
    or exists ( ( Status."Active Condition" ( [Condition: "Frailty Diagnosis"] ) ) FrailtyDiagnosis
        where FHIRBase."Prevalence Period" ( FrailtyDiagnosis ) overlaps "Measurement Period"
    )
    or exists ( ( Status."Finished Encounter" ( [Encounter: "Frailty Encounter"] ) ) FrailtyEncounter
        where FHIRBase."Normalize Interval" ( FrailtyEncounter.period ) overlaps "Measurement Period"
    )
    or exists ( [Observation: "Frailty Symptom"] FrailtySymptom
        where FHIRBase."Normalize Interval" ( FrailtySymptom.effective ) overlaps "Measurement Period"
    )

define "Two Outpatient Visits with Advanced Illness on Different Dates of Service":
  exists ( from
      "Outpatient Encounters or Discharges with Advanced Illness" OutpatientVisit1,
      "Outpatient Encounters or Discharges with Advanced Illness" OutpatientVisit2
      where OutpatientVisit2 1 day or more after OutpatientVisit1
      return OutpatientVisit1
  )

define "Outpatient Encounters or Discharges with Advanced Illness":
  if ( "Outpatient Encounters with Advanced Illness"
      union "Nonacute Inpatient Discharge with Advanced Illness"
  ) is not null then ( "Outpatient Encounters with Advanced Illness"
      union "Nonacute Inpatient Discharge with Advanced Illness"
  ) 
    else if "Outpatient Encounters with Advanced Illness" is null then "Nonacute Inpatient Discharge with Advanced Illness" 
    else if "Nonacute Inpatient Discharge with Advanced Illness" is null then "Outpatient Encounters with Advanced Illness" 
    else null

define "Outpatient Encounters with Advanced Illness":
  ( ( Status."Finished Encounter" ( [Encounter: "Outpatient"]
    union [Encounter: "Observation"]
    union [Encounter: "ED"]
    union [Encounter: "Telephone Visits"]
    union [Encounter: "Online Assessments"]
    union [Encounter: "Nonacute Inpatient"] ) ) OutpatientEncounter
    where Encounters."Encounter Has Diagnosis" ( OutpatientEncounter, [Condition: "Advanced Illness"] )
      and date from start of FHIRBase."Normalize Interval" ( OutpatientEncounter.period ) during Interval[date from start of "Measurement Period" - 1 year, date from 
      end of "Measurement Period"]) EncounterWithDiagnosis
    return date from 
    end of FHIRBase."Normalize Interval" ( EncounterWithDiagnosis.period )

define "Nonacute Inpatient Discharge with Advanced Illness":
  ( ( Claims."Medical Claims With Diagnosis" ( Claims."Medical Claims With Nonacute or Acute Inpatient Discharge" ( [Claim] ).NonacuteInpatientDischarge, "Advanced Illness ValueSet" ).ServicePeriod ) DischargeWithDiagnosis
    where date from 
    end of DischargeWithDiagnosis during Interval[date from start of "Measurement Period" - 1 year, date from 
    end of "Measurement Period"]) InpatientDischarge
    return date from 
    end of InpatientDischarge

define "Advanced Illness ValueSet":
  FHIRBase."VS Cast Function" ( "Advanced Illness" )

define "Acute Inpatient Encounter with Advanced Illness":
  exists ( ( Status."Finished Encounter" ( [Encounter: "Acute Inpatient"] ) ) InpatientEncounter
      where Encounters."Encounter Has Diagnosis" ( InpatientEncounter, [Condition: "Advanced Illness"] )
        and date from start of FHIRBase."Normalize Interval" ( InpatientEncounter.period ) during Interval[date from start of "Measurement Period" - 1 year, date from 
        end of "Measurement Period"]
  )

define "Acute Inpatient Discharge with Advanced Illness":
  exists ( ( Claims."Medical Claims With Diagnosis" ( Claims."Medical Claims With Nonacute or Acute Inpatient Discharge" ( [Claim] ).AcuteInpatientDischarge, "Advanced Illness ValueSet" ).ServicePeriod ) InpatientDischarge
      where date from 
      end of InpatientDischarge during Interval[date from start of "Measurement Period" - 1 year, date from 
      end of "Measurement Period"]
  )

define "Dementia Medications In Year Before or During Measurement Period":
  exists ( ( Status."Dispensed Medication" ( [MedicationDispense: "Dementia Medications"] ) ) DementiaMedDispensed
      where date from DementiaMedDispensed.whenHandedOver during Interval[date from start of "Measurement Period" - 1 year, date from 
      end of "Measurement Period"]
  )