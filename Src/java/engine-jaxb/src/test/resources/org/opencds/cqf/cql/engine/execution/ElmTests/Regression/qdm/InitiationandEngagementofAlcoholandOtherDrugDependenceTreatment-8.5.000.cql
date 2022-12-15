library InitiationandEngagementofAlcoholandOtherDrugDependenceTreatment version '8.5.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Hospice version '2.0.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Alcohol and Drug Dependence": 'urn:oid:2.16.840.1.113883.3.464.1003.106.12.1001'
valueset "Alcohol and Drug Dependence Treatment": 'urn:oid:2.16.840.1.113883.3.464.1003.106.12.1005'
valueset "Detoxification Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1059'
valueset "Discharge Services - Hospital Inpatient": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1007'
valueset "Discharge Services - Hospital Inpatient Same Day Discharge": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1006'
valueset "Emergency Department Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1010'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Hospital Inpatient Visit - Initial": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1004'
valueset "Hospital Observation Care - Initial": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1002'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Opiate Antagonists": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1132'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Psych Visit - Psychotherapy": 'urn:oid:2.16.840.1.113883.3.526.3.1496'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Telehealth Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1031'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Denominator":
	"Initial Population"

define "Prior Diagnosis of Dependence":
	["Diagnosis": "Alcohol and Drug Dependence"] PriorDependenceDiagnosis
		with ( "First Dependence Diagnosis" ) FirstDiagnosed
			such that PriorDependenceDiagnosis.prevalencePeriod starts 60 days or less before start of FirstDiagnosed.prevalencePeriod

define "Denominator Exclusions":
	Hospice."Has Hospice"
		or exists "Prior Diagnosis of Dependence"

define "Numerator":
	exists "Dependence Treatment Intervention Initiated"
		or exists "Treatment Initiation With Medication Order"

define "Stratification 1":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 13
				and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")< 18
	)

define "Stratification 2":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)

define "Initial Population":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 13
			and "First Dependence Diagnosis" is not null

define "First Dependence Diagnosis":
	First(["Diagnosis": "Alcohol and Drug Dependence"] InitialDependenceDiagnosis
			with("Qualifying Encounters")ValidEncounter
				such that InitialDependenceDiagnosis.prevalencePeriod starts during ValidEncounter.relevantPeriod
					and InitialDependenceDiagnosis.prevalencePeriod starts 47 days or more before day of 
					end of "Measurement Period"
			sort by start of prevalencePeriod
	)

define "Treatment Initiation With Medication Order":
	( ( ["Medication, Order": "Opiate Antagonists"] ) InitiatedMedicationTreatment
			with ( "First Dependence Diagnosis" ) FirstDiagnosed
				such that InitiatedMedicationTreatment.relevantPeriod starts 14 days or less after day of start of FirstDiagnosed.prevalencePeriod
	)

define "Qualifying Encounters":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Emergency Department Visit"]
		union ["Encounter, Performed": "Detoxification Visit"]
		union ["Encounter, Performed": "Hospital Observation Care - Initial"]
		union ["Encounter, Performed": "Hospital Inpatient Visit - Initial"]
		union ["Encounter, Performed": "Discharge Services - Hospital Inpatient Same Day Discharge"]
		union ["Encounter, Performed": "Discharge Services - Hospital Inpatient"]
		union ["Encounter, Performed": "Telehealth Services"] ) ValidEncounters
		where ValidEncounters.relevantPeriod during "Measurement Period"

define "Treatment with Single Medication Order":
	["Medication, Order": "Opiate Antagonists"] MedicationOrderOpiate
		where MedicationOrderOpiate.authorDatetime during "Measurement Period"

define "Continuation of Treatment Event":
	( ["Intervention, Performed": "Alcohol and Drug Dependence Treatment"]
		union ["Intervention, Performed": "Psych Visit - Psychotherapy"]
		union ["Encounter, Performed": "Telehealth Services"] ) ContinuedTreatment
		with ( "Treatment Initiation With Medication Order" ) TreatmentEvent
			such that ContinuedTreatment.relevantPeriod starts 34 days or less after start of TreatmentEvent.relevantPeriod

define "Dependence Treatment Intervention Continued":
	Count((["Intervention, Performed": "Alcohol and Drug Dependence Treatment"]
			union ["Intervention, Performed": "Psych Visit - Psychotherapy"]
			union ["Encounter, Performed": "Telehealth Services"])ContinuedTreatment
			with("Dependence Treatment Intervention Initiated")InitiatedTreatment
				such that ContinuedTreatment.relevantPeriod starts 34 days or less after start of InitiatedTreatment.relevantPeriod
	)>= 2

define "Treatment Initiation With Medication and Continuation of Treatment with Encounters":
	Count((["Intervention, Performed": "Alcohol and Drug Dependence Treatment"]
			union ["Intervention, Performed": "Psych Visit - Psychotherapy"]
			union ["Encounter, Performed": "Telehealth Services"])ContinuedTreatment
			with("Treatment Initiation With Medication Order")InitiatedTreatment
				such that ContinuedTreatment.relevantPeriod starts 34 days or less after start of InitiatedTreatment.relevantPeriod
	)>= 2

define "Numerator 2":
	"Dependence Treatment Intervention Continued"
		or exists ( "Treatment with Single Medication Order" ContinuedTreatment
				with "Dependence Treatment Intervention Initiated" InitiatedMedicationTreatment
					such that ContinuedTreatment.authorDatetime 34 days or less after start of InitiatedMedicationTreatment.relevantPeriod
		)
		or "Treatment Initiation With Medication and Continuation of Treatment with Encounters"
		or ( exists "Continuation of Treatment Event"
				and exists ( "Treatment with Single Medication Order" ContinuedTreatment
						with ( "Treatment Initiation With Medication Order" ) InitiatedTreatment
							such that ContinuedTreatment.authorDatetime 34 days or less after start of InitiatedTreatment.relevantPeriod
				)
		)

define "Dependence Treatment Intervention Initiated":
	( ( ["Intervention, Performed": "Alcohol and Drug Dependence Treatment"]
			union ["Intervention, Performed": "Psych Visit - Psychotherapy"]
			union ["Encounter, Performed": "Telehealth Services"] ) InitiatedTreatment
			with ( "First Dependence Diagnosis" ) FirstDiagnosed
				such that InitiatedTreatment.relevantPeriod starts 14 days or less after start of FirstDiagnosed.prevalencePeriod
	)