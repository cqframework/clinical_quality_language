/*
NOTE: Preliminary QICore 6.0.0 uplift to support connectathon 38 testing
*/
library PCSDepressionScreenAndFollowUpFHIR version '0.2.000'

using QICore version '6.0.0'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include QICoreCommon version '3.0.000' called QICoreCommon
include SupplementalDataElements version '4.0.000' called SDE

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMEDCT": 'http://snomed.info/sct'

valueset "Adolescent Depression Medications": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1567'
valueset "Adult Depression Medications": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1566'
valueset "Bipolar Disorder": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.67.1.101.1.128'
valueset "Encounter to Screen for Depression": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1916'
valueset "Follow Up for Adolescent Depression": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1569'
valueset "Follow Up for Adult Depression": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1568'
valueset "Medical Reason": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1007'
valueset "Physical Therapy Evaluation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1022'
valueset "Referral for Adolescent Depression": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1570'
valueset "Referral for Adult Depression": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1571'
valueset "Telephone Visits": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1080'

code "Adolescent depression screening assessment": '73831-0' from "LOINC" display 'Adolescent depression screening assessment'
code "Adult depression screening assessment": '73832-8' from "LOINC" display 'Adult depression screening assessment'
code "Depression screening declined (situation)": '720834000' from "SNOMEDCT" display 'Depression screening declined (situation)'
code "Depression screening negative (finding)": '428171000124102' from "SNOMEDCT" display 'Depression screening negative (finding)'
code "Depression screening positive (finding)": '428181000124104' from "SNOMEDCT" display 'Depression screening positive (finding)'

parameter "Measurement Period" Interval<DateTime> default Interval[@2025-01-01T00:00:00Z, @2026-01-01T00:00:00Z)

context Patient

define "Initial Population":
  "Patient Age 12 Years or Older at Start of Measurement Period"
    and exists ( "Qualifying Encounter During Measurement Period" )

define "Denominator":
  "Initial Population"

define "Denominator Exclusions":
  exists "History of Bipolar Diagnosis Before Qualifying Encounter"

define "Numerator":
  ( "Patient Age 12 to 16 Years at Start of Measurement Period"
      and ( "Has Most Recent Adolescent Screening Negative"
          or exists "Most Recent Adolescent Depression Screening Positive and Follow Up Provided"
      )
  )
    or ( "Patient Age 17 Years at Start of Measurement Period"
        and ( "Has Most Recent Adolescent Screening Negative"
            or exists "Most Recent Adolescent Depression Screening Positive and Follow Up Provided"
            or "Has Most Recent Adult Screening Negative"
            or exists "Most Recent Adult Depression Screening Positive and Follow Up Provided"
        )
    )
    or ( "Patient Age 18 Years or Older at Start of Measurement Period"
        and ( "Has Most Recent Adult Screening Negative"
            or exists "Most Recent Adult Depression Screening Positive and Follow Up Provided"
        )
    )

define "Denominator Exceptions":
  ( exists "Medical or Patient Reason for Not Screening Adolescent for Depression"
      and not "Has Adolescent Depression Screening"
  )
    or ( exists "Medical or Patient Reason for Not Screening Adult for Depression"
        and not "Has Adult Depression Screening"
    )

define "Follow Up Intervention for Positive Adolescent Depression Screening":
  ( ( [MedicationRequest: "Adolescent Depression Medications"] AdolescentMed
        where AdolescentMed.status in { 'active', 'completed' }
          and AdolescentMed.intent = 'order'
    )
      union ( [ServiceRequest: "Referral for Adolescent Depression"] AdolescentReferral
          where AdolescentReferral.status in { 'active', 'completed' }
      )
      union ( [Procedure: "Follow Up for Adolescent Depression"] AdolescentFollowUp
          where AdolescentFollowUp.status = 'completed'
      )
  )

define "Follow Up Intervention for Positive Adult Depression Screening":
  ( ( [MedicationRequest: "Adult Depression Medications"] AdultMed
        where AdultMed.status in { 'active', 'completed' }
          and AdultMed.intent = 'order'
    )
      union ( [ServiceRequest: "Referral for Adult Depression"] AdultReferral
          where AdultReferral.status in { 'active', 'completed' }
      )
      union ( [Procedure: "Follow Up for Adult Depression"] AdultFollowUp
          where AdultFollowUp.status = 'completed'
      )
  )

define "Has Adolescent Depression Screening":
  exists ( [ObservationScreeningAssessment: "Adolescent depression screening assessment"] AdolescentScreening
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that AdolescentScreening.effective.toInterval() 14 days or less on or before day of start of QualifyingEncounter.period
          and AdolescentScreening.value is not null
          and AdolescentScreening.status = 'final'
  )

define "Has Adult Depression Screening":
  exists ( [ObservationScreeningAssessment: "Adult depression screening assessment"] AdultScreening
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that AdultScreening.effective.toInterval() 14 days or less on or before day of start of QualifyingEncounter.period
          and AdultScreening.value is not null
          and AdultScreening.status = 'final'
  )

define "Has Most Recent Adolescent Screening Negative":
  ( "Most Recent Adolescent Depression Screening" AdolescentScreen
      where AdolescentScreen.value ~ "Depression screening negative (finding)"
  ) is not null

define "Has Most Recent Adult Screening Negative":
  ( "Most Recent Adult Depression Screening" AdultScreen
      where AdultScreen.value ~ "Depression screening negative (finding)"
  ) is not null

define "History of Bipolar Diagnosis Before Qualifying Encounter":
  [ConditionProblemsHealthConcerns: "Bipolar Disorder"] BipolarDiagnosis
    with "Qualifying Encounter During Measurement Period" QualifyingEncounter
      such that BipolarDiagnosis.prevalenceInterval() starts before QualifyingEncounter.period

define "Medical or Patient Reason for Not Screening Adolescent for Depression":
  [ObservationCancelled: code ~ "Adolescent depression screening assessment"] NoAdolescentScreen
    with "Qualifying Encounter During Measurement Period" QualifyingEncounter
      such that NoAdolescentScreen.issued during QualifyingEncounter.period
    where ( NoAdolescentScreen.notDoneReason ~ "Depression screening declined (situation)"
        or NoAdolescentScreen.notDoneReason in "Medical Reason"
    )
      and NoAdolescentScreen.status = 'cancelled'

define "Medical or Patient Reason for Not Screening Adult for Depression":
  [ObservationCancelled: code ~ "Adult depression screening assessment"] NoAdultScreen
    with "Qualifying Encounter During Measurement Period" QualifyingEncounter
      such that NoAdultScreen.issued during QualifyingEncounter.period
    where ( NoAdultScreen.notDoneReason ~ "Depression screening declined (situation)"
        or NoAdultScreen.notDoneReason in "Medical Reason"
    )
      and NoAdultScreen.status = 'cancelled'

define "Most Recent Adolescent Depression Screening":
  Last([ObservationScreeningAssessment: "Adolescent depression screening assessment"] AdolescentDepressionScreening
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that AdolescentDepressionScreening.effective.toInterval() 14 days or less on or before day of start of QualifyingEncounter.period
          and AdolescentDepressionScreening.value is not null
          and AdolescentDepressionScreening.status = 'final'
      sort by start of effective.toInterval()
  )

define "Most Recent Adult Depression Screening":
  Last([ObservationScreeningAssessment: "Adult depression screening assessment"] AdultDepressionScreening
      with "Qualifying Encounter During Measurement Period" QualifyingEncounter
        such that AdultDepressionScreening.effective.toInterval() 14 days or less on or before day of start of QualifyingEncounter.period
          and AdultDepressionScreening.value is not null
          and AdultDepressionScreening.status = 'final'
      sort by start of effective.toInterval()
  )

define "Most Recent Adolescent Depression Screening Positive and Follow Up Provided":
  from
    "Most Recent Adolescent Depression Screening" LastAdolescentScreen,
    "Follow Up Intervention for Positive Adolescent Depression Screening" FollowUpPositiveAdolescentScreen,
    "Qualifying Encounter During Measurement Period" QualifyingEncounter
    where LastAdolescentScreen.effective.toInterval ( ) starts 14 days or less on or before day of start of QualifyingEncounter.period
      and LastAdolescentScreen.value ~ "Depression screening positive (finding)"
      and ( start of FollowUpPositiveAdolescentScreen.performed.toInterval ( ) during QualifyingEncounter.period
          or FollowUpPositiveAdolescentScreen.authoredOn 2 days or less on or after day of end of QualifyingEncounter.period
      )
      and ( Coalesce(start of FollowUpPositiveAdolescentScreen.performed.toInterval(), FollowUpPositiveAdolescentScreen.authoredOn) during day of "Measurement Period" )

define "Most Recent Adult Depression Screening Positive and Follow Up Provided":
  from
    "Most Recent Adult Depression Screening" LastAdultScreen,
    "Follow Up Intervention for Positive Adult Depression Screening" FollowUpPositiveAdultScreen,
    "Qualifying Encounter During Measurement Period" QualifyingEncounter
    where LastAdultScreen.effective.toInterval() starts 14 days or less on or before day of start of QualifyingEncounter.period
      and LastAdultScreen.value ~ "Depression screening positive (finding)"
      and ( start of FollowUpPositiveAdultScreen.performed.toInterval() during QualifyingEncounter.period
          or FollowUpPositiveAdultScreen.authoredOn 2 days or less on or after day of end of QualifyingEncounter.period
      )
      and ( Coalesce(start of FollowUpPositiveAdultScreen.performed.toInterval(), FollowUpPositiveAdultScreen.authoredOn) during day of "Measurement Period" )

define "Patient Age 12 to 16 Years at Start of Measurement Period":
  AgeInYearsAt(date from start of "Measurement Period") in Interval[12, 16]

define "Patient Age 12 Years or Older at Start of Measurement Period":
  AgeInYearsAt(date from start of "Measurement Period") >= 12

define "Patient Age 17 Years at Start of Measurement Period":
  AgeInYearsAt(date from start of "Measurement Period") = 17

define "Patient Age 18 Years or Older at Start of Measurement Period":
  AgeInYearsAt(date from start of "Measurement Period") >= 18

define "Qualifying Encounter During Measurement Period":
  ( [Encounter: type in "Encounter to Screen for Depression"]
    union [Encounter: type in "Physical Therapy Evaluation"]
    union [Encounter: type in "Telephone Visits"] ) QualifyingEncounter
    where QualifyingEncounter.period during day of "Measurement Period"
      and QualifyingEncounter.status = 'finished'

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Payer":
  SDE."SDE Payer"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"