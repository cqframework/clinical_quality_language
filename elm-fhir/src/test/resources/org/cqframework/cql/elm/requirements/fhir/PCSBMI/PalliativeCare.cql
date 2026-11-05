library PalliativeCare version '1.7.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers
include QICoreCommon version '1.5.000' called QICoreCommon
include Status version '1.6.000' called Status

codesystem "LOINC": 'http://loinc.org'

valueset "Palliative Care Encounter": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1090'
valueset "Palliative Care Intervention": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1135'
valueset "Palliative Care Diagnosis": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.1167'

code "Functional Assessment of Chronic Illness Therapy - Palliative Care Questionnaire (FACIT-Pal)": '71007-9' from "LOINC" display 'Functional Assessment of Chronic Illness Therapy - Palliative Care Questionnaire (FACIT-Pal)'

parameter "Measurement Period" Interval<DateTime>
    default Interval[@2024-01-01T00:00:00.0, @2025-01-01T00:00:00.0)

context Patient

define "Has Palliative Care in the Measurement Period":
  exists ((([Observation: "Functional Assessment of Chronic Illness Therapy - Palliative Care Questionnaire (FACIT-Pal)"]).isAssessmentPerformed()) PalliativeAssessment
      where PalliativeAssessment.effective.toInterval() overlaps day of "Measurement Period"
  )
    or exists ([Condition: "Palliative Care Diagnosis"] PalliativeDiagnosis
        where PalliativeDiagnosis.prevalenceInterval() overlaps day of "Measurement Period"
    )
    or exists ((([Encounter: "Palliative Care Encounter"]).isEncounterPerformed()) PalliativeEncounter
        where PalliativeEncounter.period.toInterval() overlaps day of "Measurement Period"
    )
    or exists ((([Procedure: "Palliative Care Intervention"]).isInterventionPerformed()) PalliativeIntervention
        where PalliativeIntervention.performed.toInterval() overlaps day of "Measurement Period"
    )