library HBPComponent version '0.0.001'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.001' called FHIRHelpers
include SupplementalDataElementsFHIR4 version '2.0.000' called SDE
include MATGlobalCommonFunctionsFHIR4 version '5.0.000' called Global

codesystem "LOINC": 'http://loinc.org'

valueset "Diagnosis of Hypertension": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.263'
valueset "Dietary Recommendations": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1515'
valueset "Encounter to Screen for Blood Pressure": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1920'
valueset "Ethnicity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.837'
valueset "Finding of Elevated Blood Pressure or Hypertension": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1047.514'
valueset "Follow Up Within 4 Weeks": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1578'
valueset "Follow Up Within One Year": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1579'
valueset "Laboratory Tests for Hypertension": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1482'
valueset "Lifestyle Recommendation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1581'
valueset "Medical Reason": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1007'
valueset "ONC Administrative Sex": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1'
valueset "Patient Declined": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1582'
valueset "Pharmacologic Therapy for Hypertension": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.1577'
valueset "Recommendation to Increase Physical Activity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1518'
valueset "Referral or Counseling for Alcohol Consumption": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1583'
valueset "Referral to Primary Care or Alternate Provider": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1580'
valueset "Weight Reduction Recommended": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1510'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Diastolic blood pressure": '8462-4' from "LOINC" display 'Diastolic blood pressure'
code "EKG 12 channel panel": '34534-8' from "LOINC" display 'EKG 12 channel panel'
code "EKG study": '11524-6' from "LOINC" display 'EKG study'
code "Systolic blood pressure": '8480-6' from "LOINC" display 'Systolic blood pressure'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Payer":
  SDE."SDE Payer"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"

define "Encounter with Second Hypertensive Reading":
  "Encounter with High Blood Pressure Reading"
      intersect "Encounter with High Blood Pressure Reading Within Year Prior"

define "Numerator":
  "Encounter with Normal Blood Pressure Reading"
      union ( "Encounter with Prehypertensive Blood Pressure Reading"
          intersect ( ( "Encounter with Order for Intervention for Hypertension"
                intersect "Encounter with Order for Follow Up Within One Year"
            )
              union "Encounter with Referral to Alternate Provider for High Blood Pressure Reading"
          )
      )
      union ( "Encounter with First Hypertensive Reading"
          intersect ( ( "Encounter with Order for Intervention for Hypertension"
                intersect "Encounter with Order for Follow Up Within 4 Weeks"
            )
              union "Encounter with Referral to Alternate Provider for High Blood Pressure Reading"
          )
      )
      union ( "Encounter with Second Hypertensive Reading"
          intersect ( ( "Encounter with Order for Intervention for Hypertension"
                intersect ( "Encounter with Order for Laboratory Test for Hypertension"
                    union "Encounter with Order for Electrocardiogram for Hypertension"
                    union "Encounter with Order for Hypertension Medication"
                )
            )
              union "Encounter with Referral to Alternate Provider for High Blood Pressure Reading"
          )
      )

define "Encounter with First Hypertensive Reading":
  "Encounter with High Blood Pressure Reading"
      except "Encounter with High Blood Pressure Reading Within Year Prior"

define "Initial Population":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with [Patient] BirthDate
        such that Global."CalendarAgeInYearsAt" ( FHIRHelpers.ToDate ( BirthDate.birthDate ), start of "Measurement Period" ) >= 18

define "Encounter with Order for Follow Up Within One Year":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with [ServiceRequest: "Follow Up Within One Year"] FollowupOneYear
        such that FollowupOneYear.authoredOn same day as start of QualifyingEncounter.period

define "Encounter with Order for Follow Up Within 4 Weeks":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with [ServiceRequest: "Follow Up Within 4 Weeks"] Followup4Weeks
        such that Followup4Weeks.authoredOn same day as start of QualifyingEncounter.period

define "Encounter with Order for Hypertension Medication":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with [MedicationRequest: "Pharmacologic Therapy for Hypertension"] AntihypertensiveMedication
        such that AntihypertensiveMedication.authoredOn same day as start of QualifyingEncounter.period

define "Encounter with Order for Laboratory Test for Hypertension":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with [ServiceRequest: "Laboratory Tests for Hypertension"] HypertensionLabs
        such that HypertensionLabs.authoredOn same day as start of QualifyingEncounter.period

define "Encounter with Referral to Alternate Provider for High Blood Pressure Reading":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with [ServiceRequest: "Referral to Primary Care or Alternate Provider"] Referral
        such that Referral.authoredOn same day as start of QualifyingEncounter.period
          and Referral.reasonCode in "Finding of Elevated Blood Pressure or Hypertension"

define "Encounter with Order for Hypertension Follow Up Declined by Patient":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with ( [ServiceRequest: "EKG 12 channel panel"]
        union [ServiceRequest: "EKG study"]
        union [ServiceRequest: "Follow Up Within One Year"]
        union [ServiceRequest: "Follow Up Within 4 Weeks"]
        union [ServiceRequest: "Referral to Primary Care or Alternate Provider"]
        union [ServiceRequest: "Lifestyle Recommendation"]
        union [ServiceRequest: "Weight Reduction Recommended"]
        union [ServiceRequest: "Dietary Recommendations"]
        union [ServiceRequest: "Recommendation to Increase Physical Activity"]
        union [ServiceRequest: "Referral or Counseling for Alcohol Consumption"]
        union [ServiceRequest: "Laboratory Tests for Hypertension"]
        union [MedicationRequest: "Pharmacologic Therapy for Hypertension"] ) NoFollowUpOrdered
        such that NoFollowUpOrdered.doNotPerform is true
          and NoFollowUpOrdered.code in "Patient Declined"
          and NoFollowUpOrdered.authoredOn same day as start of QualifyingEncounter.period

define "Encounter with Medical Reason for Not Obtaining or Patient Declined Blood Pressure Measurement":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with ( [Observation: "Systolic blood pressure"]
        union [Observation: "Diastolic blood pressure"] ) NoBPScreen
        such that ( NoBPScreen.code in "Medical Reason"
            or NoBPScreen.code in "Patient Declined"
        )
          and NoBPScreen.effective same day as start of QualifyingEncounter.period
          and "Not Done"(NoBPScreen)
          and NoBPScreen.status = 'final'

define "Denominator Exceptions":
  "Encounter with Medical Reason for Not Obtaining or Patient Declined Blood Pressure Measurement"
      union "Encounter with Order for Hypertension Follow Up Declined by Patient"

define "Denominator Exclusions":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with [Condition: "Diagnosis of Hypertension"] Hypertension
        such that Global."Prevalence Period" ( Hypertension ) overlaps before QualifyingEncounter.period

define "Denominator":
  "Initial Population"

define "Encounter with Order for Electrocardiogram for Hypertension":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with ( [ServiceRequest: "EKG 12 channel panel"]
        union [ServiceRequest: "EKG study"] ) ECG
        such that ECG.authoredOn same day as start of QualifyingEncounter.period

define "Encounter with Order for Intervention for Hypertension":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      with ( [ServiceRequest: "Lifestyle Recommendation"]
        union [ServiceRequest: "Weight Reduction Recommended"]
        union [ServiceRequest: "Dietary Recommendations"]
        union [ServiceRequest: "Recommendation to Increase Physical Activity"]
        union [ServiceRequest: "Referral or Counseling for Alcohol Consumption"] ) HighBloodPressureIntervention
        such that HighBloodPressureIntervention.authoredOn same day as start of QualifyingEncounter.period

define "Encounter with High Blood Pressure Reading":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      let EncounterLastSystolicBP: Last([Observation: "Systolic blood pressure"] SystolicBP
          where SystolicBP.effective during QualifyingEncounter.period
          sort by effective
      ),
      EncounterLastDiastolicBP: Last([Observation: "Diastolic blood pressure"] DiastolicBP
          where DiastolicBP.effective during QualifyingEncounter.period
          sort by effective
      )
      where EncounterLastSystolicBP.value > 0 'mm[Hg]'
        and EncounterLastDiastolicBP.value > 0 'mm[Hg]'
        and ( EncounterLastSystolicBP.value >= 140 'mm[Hg]'
            or EncounterLastDiastolicBP.value >= 90 'mm[Hg]'
        )

define "Encounter with High Blood Pressure Reading Within Year Prior":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      let EncounterLastSystolicBP: Last([Observation: "Systolic blood pressure"] SystolicBP
          where SystolicBP.effective 1 year or less before start of QualifyingEncounter.period
          sort by effective
      ),
      EncounterLastDiastolicBP: Last([Observation: "Diastolic blood pressure"] DiastolicBP
          where DiastolicBP.effective 1 year or less before start of QualifyingEncounter.period
          sort by effective
      )
      where EncounterLastSystolicBP.value > 0 'mm[Hg]'
        and EncounterLastDiastolicBP.value > 0 'mm[Hg]'
        and ( EncounterLastSystolicBP.value >= 140 'mm[Hg]'
            or EncounterLastDiastolicBP.value >= 90 'mm[Hg]'
        )

define "Encounter with Normal Blood Pressure Reading":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      let EncounterLastSystolicBP: Last([Observation: "Systolic blood pressure"] SystolicBP
          where SystolicBP.effective during QualifyingEncounter.period
          sort by effective
      ),
      EncounterLastDiastolicBP: Last([Observation: "Diastolic blood pressure"] DiastolicBP
          where DiastolicBP.effective during QualifyingEncounter.period
          sort by effective
      )
      where EncounterLastSystolicBP.value included in Interval[1 'mm[Hg]', 120 'mm[Hg]' )
        and EncounterLastDiastolicBP.value included in Interval[1 'mm[Hg]', 80 'mm[Hg]' )

define "Encounter with Prehypertensive Blood Pressure Reading":
  "Qualifying Encounter During Measurement Period" QualifyingEncounter
      let EncounterLastSystolicBP: Last([Observation: "Systolic blood pressure"] SystolicBP
          where SystolicBP.effective during QualifyingEncounter.period
          sort by effective
      ),
      EncounterLastDiastolicBP: Last([Observation: "Diastolic blood pressure"] DiastolicBP
          where DiastolicBP.effective during QualifyingEncounter.period
          sort by effective
      )
      where EncounterLastSystolicBP.value included in Interval[1 'mm[Hg]', 140 'mm[Hg]' )
        and EncounterLastDiastolicBP.value included in Interval[1 'mm[Hg]', 90 'mm[Hg]' )
        and ( EncounterLastSystolicBP.value >= 120 'mm[Hg]'
            or EncounterLastDiastolicBP.value >= 80 'mm[Hg]'
        )

define "Qualifying Encounter During Measurement Period":
  [Encounter: "Encounter to Screen for Blood Pressure"] ValidEncounter
      where ValidEncounter.period during "Measurement Period"

define function "Not Done"(res DomainResource ):
  if ( res R
      where ( "GetModiferExtension"(R, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDone').value = true )
  ) is not null then true
    else false

define function "GetModifierExtensions"(res DomainResource, url String ):
  res.modifierExtension E
    where E.url = ( url )
    return E

define function "GetModiferExtension"(res DomainResource, url String ):
  singleton from "GetModifierExtensions"(res, url)
