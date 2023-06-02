library AdvancedIllnessandFrailtyExclusionECQM version '4.0.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1' 

valueset "Acute Inpatient": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1083' 
valueset "Advanced Illness": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1082' 
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014' 
valueset "Dementia Medications": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1510' 
valueset "ED": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1085' 
valueset "Frailty Device": 'urn:oid:2.16.840.1.113883.3.464.1003.118.12.1300' 
valueset "Frailty Diagnosis": 'urn:oid:2.16.840.1.113883.3.464.1003.113.12.1074' 
valueset "Frailty Encounter": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1088' 
valueset "Frailty Symptom": 'urn:oid:2.16.840.1.113883.3.464.1003.113.12.1075' 
valueset "Nonacute Inpatient": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1084' 
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012' 
valueset "Observation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1086' 
valueset "Outpatient": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1087' 

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Outpatient Encounters with Advanced Illness":
	( ["Encounter, Performed": "Outpatient"]
		union ["Encounter, Performed": "Observation"]
		union ["Encounter, Performed": "ED"]
		union ["Encounter, Performed": "Nonacute Inpatient"] ) OutpatientEncounter
		where exists ( OutpatientEncounter.diagnoses Diagnosis
				where Diagnosis in "Advanced Illness"
		)
			and OutpatientEncounter.relevantPeriod starts 2 years or less before 
			end of "Measurement Period"

define "Long Term Care Periods During Measurement Period":
	( ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Nursing Facility Visit"] ) LongTermFacilityEncounter
		where LongTermFacilityEncounter.relevantPeriod overlaps "Measurement Period"
		return LongTermFacilityEncounter.relevantPeriod
			intersect "Measurement Period"

define "Inpatient Encounter with Advanced Illness":
	["Encounter, Performed": "Acute Inpatient"] InpatientEncounter
		where exists ( InpatientEncounter.diagnoses Diagnosis
				where Diagnosis in "Advanced Illness"
		)
			and InpatientEncounter.relevantPeriod starts 2 years or less before 
			end of "Measurement Period"

define "Dementia Medications In Year Before or During Measurement Period":
	["Medication, Active": "Dementia Medications"] DementiaMed
		where DementiaMed.relevantPeriod overlaps Interval[( start of "Measurement Period" - 1 year ), 
		end of "Measurement Period"]

define "Has Criteria Indicating Frailty":
	exists ( ["Device, Order": "Frailty Device"] FrailtyDeviceOrder
			where FrailtyDeviceOrder.authorDatetime during "Measurement Period"
	)
		or exists ( ["Device, Applied": "Frailty Device"] FrailtyDeviceApplied
				where FrailtyDeviceApplied.relevantPeriod overlaps "Measurement Period"
		)
		or exists ( ["Diagnosis": "Frailty Diagnosis"] FrailtyDiagnosis
				where FrailtyDiagnosis.prevalencePeriod overlaps "Measurement Period"
		)
		or exists ( ["Encounter, Performed": "Frailty Encounter"] FrailtyEncounter
				where FrailtyEncounter.relevantPeriod overlaps "Measurement Period"
		)
		or exists ( ["Symptom": "Frailty Symptom"] FrailtySymptom
				where FrailtySymptom.prevalencePeriod overlaps "Measurement Period"
		)

define "Advanced Illness and Frailty Exclusion Including Over Age 80":
	//If the measure includes populations age 80 and older, then use this logic:
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where ( Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")in Interval[66, 80])
				and "Has Criteria Indicating Frailty"
				and ( Count("Outpatient Encounters with Advanced Illness")>= 2
						or exists ( "Inpatient Encounter with Advanced Illness" )
						or exists "Dementia Medications In Year Before or During Measurement Period"
				)
	)
		or exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
				where ( Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 81 )
					and "Has Criteria Indicating Frailty"
		)

define "Advanced Illness and Frailty Exclusion Including Under Age 80":
	//If the measure does NOT include populations age 80 and older, then use this logic:
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 65
				and "Has Criteria Indicating Frailty"
				and ( Count("Outpatient Encounters with Advanced Illness")>= 2
						or exists ( "Inpatient Encounter with Advanced Illness" )
						or exists "Dementia Medications In Year Before or During Measurement Period"
				)
	)

define "Has Spent More Than 90 Days in Long Term Care":
	"Days Spent in Long Term Care During Measurement Period" > 90

define "Days Spent in Long Term Care During Measurement Period":
	"CumulativeDays"("Long Term Care Periods During Measurement Period")

define function "CumulativeDays"(Intervals List<Interval<DateTime>> ):
	Sum((collapse Intervals)CollapsedInterval
			return all duration in days of CollapsedInterval
	)

