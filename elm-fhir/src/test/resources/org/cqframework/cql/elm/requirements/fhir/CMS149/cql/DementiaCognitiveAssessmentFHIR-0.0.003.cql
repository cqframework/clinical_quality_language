library DementiaCognitiveAssessmentFHIR version '0.0.003'

using QICore version '4.1.1'

/*This spec aligns with AU2022/PY2023 QDM version*/


include FHIRHelpers version '4.1.000' called FHIRHelpers
include SupplementalDataElements version '3.1.000' called SDE
include CQMCommon version '1.0.000' called CQMCommon
include QICoreCommon version '1.2.000' called QICoreCommon

codesystem "ActCode": 'http://terminology.hl7.org/CodeSystem/v3-ActCode'

valueset "Behavioral/Neuropsych Assessment": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1023'
valueset "Care Services in Long Term Residential Facility": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Cognitive Assessment": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1332'
valueset "Dementia & Mental Degenerations": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1005'
valueset "Face-to-Face Interaction": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1048'
valueset "Home Healthcare Services": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Nursing Facility Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Occupational Therapy Evaluation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1011'
valueset "Office Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Outpatient Consultation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Patient Provider Interaction": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1012'
valueset "Patient Reason": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1008'
valueset "Psych Visit Diagnostic Evaluation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1492'
valueset "Psych Visit Psychotherapy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1496'
valueset "Standardized Tools for Assessment of Cognition": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1006'

code "virtual": 'VR' from "ActCode" display 'virtual'
code "AMB": 'AMB' from "ActCode" display 'Ambulatory'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"

define "SDE Payer":
  SDE."SDE Payer"

define "Initial Population":
  exists "Dementia Encounter During Measurement Period"
    and ( Count("Qualifying Encounter During Measurement Period") >= 2 )

define "Denominator":
  "Initial Population"

define "Numerator":
  exists "Assessment of Cognition Using Standardized Tools or Alternate Methods"

define "Denominator Exceptions":
  exists "Patient Reason for Not Performing Assessment of Cognition Using Standardized Tools or Alternate Methods"

define "Encounter to Assess Cognition":
  ["Encounter": "Psych Visit Diagnostic Evaluation"]
    union ["Encounter": "Nursing Facility Visit"]
    union ["Encounter": "Care Services in Long Term Residential Facility"]
    union ["Encounter": "Home Healthcare Services"]
    union ["Encounter": "Psych Visit Psychotherapy"]
    union ["Encounter": "Behavioral/Neuropsych Assessment"]
    union ["Encounter": "Occupational Therapy Evaluation"]
    union ["Encounter": "Office Visit"]
    union ["Encounter": "Outpatient Consultation"]

define "Qualifying Encounter During Measurement Period":
  ( "Encounter to Assess Cognition"
    union ["Encounter": "Patient Provider Interaction"] ) ValidEncounter
    where ValidEncounter.period during "Measurement Period"
      and ValidEncounter.class ~ "AMB"
      and ValidEncounter.status = 'finished'

define "Patient Reason for Not Performing Assessment of Cognition Using Standardized Tools or Alternate Methods":
  ( [ObservationNotDone: code in "Standardized Tools for Assessment of Cognition"]
    union [ObservationNotDone: code in "Cognitive Assessment"] ) NoCognitiveAssessment
    with "Dementia Encounter During Measurement Period" EncounterDementia
      such that NoCognitiveAssessment.issued during EncounterDementia.period
    where NoCognitiveAssessment.status = 'cancelled'
      and NoCognitiveAssessment.notDoneReason in "Patient Reason"

define "Assessment of Cognition Using Standardized Tools or Alternate Methods":
  ( ["Observation": "Standardized Tools for Assessment of Cognition"]
    union ["Observation": "Cognitive Assessment"] ) CognitiveAssessment
    with "Dementia Encounter During Measurement Period" EncounterDementia
      such that QICoreCommon."ToInterval" ( CognitiveAssessment.issued ) starts 12 months or less on or before day of end of EncounterDementia.period
    where CognitiveAssessment.value is not null

define "Dementia Encounter During Measurement Period":
  "Encounter to Assess Cognition" EncounterAssessCognition
    with [Condition: "Dementia & Mental Degenerations"] Dementia
      such that CQMCommon."ToDateInterval" ( EncounterAssessCognition.period ) during "Measurement Period"
        and QICoreCommon."ToPrevalenceInterval" ( Dementia ) overlaps EncounterAssessCognition.period