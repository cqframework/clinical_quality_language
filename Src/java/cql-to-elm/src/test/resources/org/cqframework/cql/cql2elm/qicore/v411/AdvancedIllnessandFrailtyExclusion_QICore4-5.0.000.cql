library AdvancedIllnessandFrailtyExclusion_QICore4 version '5.0.000'

/*
@update: @@BTR 2020-03-31 ->
Incremented version to 5.0.000
Updated FHIR version to 4.0.1
@@
*/

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'

include MATGlobalCommonFunctions_QICore4 version '5.0.000' called Global

codesystem "LOINC": 'http://loinc.org'

valueset "Acute Inpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1083'
valueset "Advanced Illness": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.110.12.1082'
valueset "Care Services in Long-Term Residential Facility": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Dementia Medications": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.196.12.1510'
valueset "ED": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1085'
valueset "Frailty Device": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.118.12.1300'
valueset "Frailty Diagnosis": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.113.12.1074'
valueset "Frailty Encounter": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1088'
valueset "Frailty Symptom": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.113.12.1075'
valueset "Nonacute Inpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1084'
valueset "Nursing Facility Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Observation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1086'
valueset "Outpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1087'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>
  default Interval[@2019-01-01T00:00:00.0, @2020-01-01T00:00:00.0)

context Patient

define "Advanced Illness Diagnoses":
    [Condition: "Advanced Illness"] AdvancedIllnessDiagnosis

/*
ISSUE: Encounter.diagnosis element not being translated due to issue with ModelInfo generation
define "Outpatient Encounters with Advanced Illness":
	( [Encounter: "Outpatient"]
		union [Encounter: "Observation"]
		union [Encounter: "ED"]
		union [Encounter: "Nonacute Inpatient"] ) OutpatientEncounter
		with [Condition: "Advanced Illness"] AdvancedIllnessDiagnosis
            such that exists (
                OutpatientEncounter.diagnosis.condition EncounterDiagnosis
                    where EndsWith(EncounterDiagnosis.reference, AdvancedIllnessDiagnosis.id)
            )
            and OutpatientEncounter.period starts 2 years or less before
			end of "Measurement Period"
*/

define "Long Term Care Periods During Measurement Period":
	( [Encounter: "Care Services in Long-Term Residential Facility"]
		union [Encounter: "Nursing Facility Visit"] ) LongTermFacilityEncounter
		where LongTermFacilityEncounter.period overlaps "Measurement Period"
		return LongTermFacilityEncounter.period
			intersect "Measurement Period"

/*
ISSUE: Encounter.diagnosis element not being translated
define "Inpatient Encounter with Advanced Illness":
	[Encounter: "Acute Inpatient"] InpatientEncounter
		with [Condition: "Advanced Illness"] AdvancedIllnessDiagnosis
            such that exists (
                InpatientEncounter.diagnosis.condition EncounterDiagnosis
                    where EndsWith(EncounterDiagnosis.reference, AdvancedIllnessDiagnosis.id)
            )
			and InpatientEncounter.period starts 2 years or less before
			end of "Measurement Period"
*/

define "Dementia Medications In Year Before or During Measurement Period":
	["MedicationDispense": "Dementia Medications"] DementiaMed
		where DementiaMed.whenHandedOver during day of Interval[
            ( start of "Measurement Period" - 1 year ), end of "Measurement Period"
        ]

define "Has Criteria Indicating Frailty":
	exists ( [DeviceRequest: "Frailty Device"] FrailtyDeviceOrder
			where FrailtyDeviceOrder.authoredOn during "Measurement Period"
	)
		or exists ( [DeviceUseStatement: "Frailty Device"] FrailtyDeviceUse
				where Global."Normalize Interval"(FrailtyDeviceUse.timing) overlaps "Measurement Period"
		)
		or exists ( [Condition: "Frailty Diagnosis"] FrailtyDiagnosis
				where Global."Prevalence Period"(FrailtyDiagnosis) overlaps "Measurement Period"
		)
		or exists ( [Encounter: "Frailty Encounter"] FrailtyEncounter
				where FrailtyEncounter.period overlaps "Measurement Period"
		)
		or exists ( [Observation: "Frailty Symptom"] FrailtySymptomObservation
				where Global."Normalize Interval"(FrailtySymptomObservation.effective) overlaps "Measurement Period"
		)

/*
ISSUE: Encounter.diagnosis element missing
define "Advanced Illness and Frailty Exclusion Including Over Age 80":
	//If the measure includes populations age 80 and older, then use this logic:
	(
        Global."CalendarAgeInYearsAt"(Patient.birthDate, start of "Measurement Period")in Interval[66, 80]
            and "Has Criteria Indicating Frailty"
            and ( Count("Outpatient Encounters with Advanced Illness")>= 2
                or exists ( "Inpatient Encounter with Advanced Illness" )
                or exists "Dementia Medications In Year Before or During Measurement Period"
            )
	)
		or (
            Global."CalendarAgeInYearsAt"(FHIRHelpers.ToDate(Patient.birthDate), start of "Measurement Period")>= 81
			    and "Has Criteria Indicating Frailty"
		)

define "Advanced Illness and Frailty Exclusion Including Under Age 80":
	//If the measure does NOT include populations age 80 and older, then use this logic:
	Global."CalendarAgeInYearsAt"(Patient.birthDate, start of "Measurement Period")>= 65
        and "Has Criteria Indicating Frailty"
        and ( Count("Outpatient Encounters with Advanced Illness")>= 2
            or exists ( "Inpatient Encounter with Advanced Illness" )
            or exists "Dementia Medications In Year Before or During Measurement Period"
        )
*/

define "Has Spent More Than 90 Days in Long Term Care":
	"Days Spent in Long Term Care During Measurement Period" > 90

define "Days Spent in Long Term Care During Measurement Period":
	"CumulativeDays"("Long Term Care Periods During Measurement Period")

define function "CumulativeDays"(Intervals List<Interval<DateTime>> ):
	Sum((collapse Intervals)CollapsedInterval
			return all duration in days of CollapsedInterval
	)
