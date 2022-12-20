library DementiaCognitiveAssessment version '9.2.000'

using QDM version '5.5'

valueset "Behavioral/Neuropsych Assessment": 'urn:oid:2.16.840.1.113883.3.526.3.1023'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Cognitive Assessment": 'urn:oid:2.16.840.1.113883.3.526.3.1332'
valueset "Dementia & Mental Degenerations": 'urn:oid:2.16.840.1.113883.3.526.3.1005'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Occupational Therapy Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1011'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Patient Provider Interaction": 'urn:oid:2.16.840.1.113883.3.526.3.1012'
valueset "Patient Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Psych Visit - Diagnostic Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1492'
valueset "Psych Visit - Psychotherapy": 'urn:oid:2.16.840.1.113883.3.526.3.1496'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Standardized Tools for Assessment of Cognition": 'urn:oid:2.16.840.1.113883.3.526.3.1006'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Dementia Encounter":
  "Face to Face Encounter During Measurement Period" ValidFaceToFaceEncounter
    with ["Diagnosis": "Dementia & Mental Degenerations"] Dementia
      such that Dementia.prevalencePeriod overlaps ValidFaceToFaceEncounter.relevantPeriod

define "Denominator":
  "Initial Population"

define "Face to Face Encounter During Measurement Period":
  ( ["Encounter, Performed": "Psych Visit - Diagnostic Evaluation"]
    union ["Encounter, Performed": "Nursing Facility Visit"]
    union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
    union ["Encounter, Performed": "Home Healthcare Services"]
    union ["Encounter, Performed": "Psych Visit - Psychotherapy"]
    union ["Encounter, Performed": "Behavioral/Neuropsych Assessment"]
    union ["Encounter, Performed": "Occupational Therapy Evaluation"]
    union ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Outpatient Consultation"] ) FaceToFaceEncounter
    where FaceToFaceEncounter.relevantPeriod during "Measurement Period"

define "Initial Population":
  exists "Dementia Encounter"
    and ( Count("Qualifying Encounter")>= 2 )

define "Numerator":
  exists "Assessment of Cognition Using Standardized Tools or Alternate Methods"

define "Qualifying Encounter":
  ( ["Encounter, Performed": "Psych Visit - Diagnostic Evaluation"]
    union ["Encounter, Performed": "Nursing Facility Visit"]
    union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
    union ["Encounter, Performed": "Home Healthcare Services"]
    union ["Encounter, Performed": "Patient Provider Interaction"]
    union ["Encounter, Performed": "Psych Visit - Psychotherapy"]
    union ["Encounter, Performed": "Behavioral/Neuropsych Assessment"]
    union ["Encounter, Performed": "Occupational Therapy Evaluation"]
    union ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Outpatient Consultation"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Denominator Exceptions":
  exists "Patient Reason for Not Performing Assessment of Cognition Using Standardized Tools or Alternate Methods"

define "Assessment of Cognition Using Standardized Tools or Alternate Methods":
  ( ["Assessment, Performed": "Standardized Tools for Assessment of Cognition"]
    union ["Intervention, Performed": "Cognitive Assessment"] ) CognitiveAssessment
    with "Dementia Encounter" EncounterDementia
      such that CognitiveAssessment.relevantDatetime 12 months or less on or before day of 
      end of EncounterDementia.relevantPeriod
    where CognitiveAssessment.result is not null

define "Patient Reason for Not Performing Assessment of Cognition Using Standardized Tools or Alternate Methods":
  ( ["Assessment, Not Performed": "Standardized Tools for Assessment of Cognition"]
    union ["Intervention, Not Performed": "Cognitive Assessment"] ) NoCognitiveAssessment
    with "Dementia Encounter" EncounterDementia
      such that NoCognitiveAssessment.authorDatetime during EncounterDementia.relevantPeriod
    where NoCognitiveAssessment.negationRationale in "Patient Reason"