library DepressionUtilizationofthePHQ9Tool version '8.4.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Bipolar Disorder": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.128'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Contact or Office Visit": 'urn:oid:2.16.840.1.113762.1.4.1080.5'
valueset "Dysthymia": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.254'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Major Depression Including Remission": 'urn:oid:2.16.840.113883.3.67.1.101.3.2444'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Palliative Care": 'urn:oid:2.16.840.1.113883.3.600.1.1579'
valueset "Palliative care encounter": 'urn:oid:2.16.840.1.113883.3.600.1.1575'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Personality Disorder": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.246'
valueset "Pervasive Developmental Disorder": 'urn:oid:2.16.840.1.113883.3.464.1003.105.12.1152'
valueset "PHQ 9 and PHQ 9M Tools": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.263'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Schizophrenia or Psychotic Disorder": 'urn:oid:2.16.840.1.113883.3.464.1003.105.12.1104'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Dead (finding)": '419099009' from "SNOMEDCT" display 'Dead (finding)'

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

define "Denominator 1":
	"Initial Population 1"

define "Denominator 2":
	"Initial Population 2"

define "Denominator 3":
	"Initial Population 3"

define "Disorder Diagnoses Overlaps Depression Encounter in May through August":
	( ["Diagnosis": "Bipolar Disorder"]
		union ["Diagnosis": "Personality Disorder"]
		union ["Diagnosis": "Schizophrenia or Psychotic Disorder"]
		union ["Diagnosis": "Pervasive Developmental Disorder"] ) DisorderDiagnoses
		with "Depression Encounter in May through August" DepressionEncounter
			such that DisorderDiagnoses.prevalencePeriod overlaps DepressionEncounter.relevantPeriod

define "Initial Population 1":
	exists ( "Depression Encounter in September through December" )

define "Initial Population 2":
	exists ( "Depression Encounter in May through August" )

define "Initial Population 3":
	exists ( "Depression Encounter in January through April" )

define "Palliative Care Order Before End of December":
	["Intervention, Order": "Palliative Care"] OrderedPalliativeCare
		where OrderedPalliativeCare.authorDatetime before 
		end of "September through December of Measurement Period"

define "Palliative Care Order Before End of April":
	["Intervention, Order": "Palliative Care"] OrderedPalliativeCare
		where OrderedPalliativeCare.authorDatetime before 
		end of "January through April of Measurement Period"

define "Palliative Care Order Before End of August":
	["Intervention, Order": "Palliative Care"] OrderedPalliativeCare
		where OrderedPalliativeCare.authorDatetime before 
		end of "May through August of Measurement Period"

define "Encounter Palliative Care Overlaps September through December":
	["Encounter, Performed": "Palliative care encounter"] PalliativeCareEncounter
		where PalliativeCareEncounter.relevantPeriod overlaps "September through December of Measurement Period"

define "Encounter Palliative Care Overlaps May through August":
	["Encounter, Performed": "Palliative care encounter"] PalliativeCareEncounter
		where PalliativeCareEncounter.relevantPeriod overlaps "May through August of Measurement Period"

define "Encounter Palliative Care Overlaps January through April":
	["Encounter, Performed": "Palliative care encounter"] PalliativeCareEncounter
		where PalliativeCareEncounter.relevantPeriod overlaps "January through April of Measurement Period"

define "Depression Diagnoses":
	["Diagnosis": "Major Depression Including Remission"]
		union ["Diagnosis": "Dysthymia"]

define "Disorder Diagnoses Overlaps Depression Encounter in September through December":
	( ["Diagnosis": "Bipolar Disorder"]
		union ["Diagnosis": "Personality Disorder"]
		union ["Diagnosis": "Schizophrenia or Psychotic Disorder"]
		union ["Diagnosis": "Pervasive Developmental Disorder"] ) DisorderDiagnoses
		with "Depression Encounter in September through December" DepressionEncounter
			such that DisorderDiagnoses.prevalencePeriod overlaps DepressionEncounter.relevantPeriod

define "Disorder Diagnoses Overlaps Depression Encounter in January through April":
	( ["Diagnosis": "Bipolar Disorder"]
		union ["Diagnosis": "Personality Disorder"]
		union ["Diagnosis": "Schizophrenia or Psychotic Disorder"]
		union ["Diagnosis": "Pervasive Developmental Disorder"] ) DisorderDiagnoses
		with "Depression Encounter in January through April" DepressionEncounter
			such that DisorderDiagnoses.prevalencePeriod overlaps DepressionEncounter.relevantPeriod

define "January through April of Measurement Period":
	Interval[start of "Measurement Period", start of "Measurement Period" + 4 months )

define "May through August of Measurement Period":
	Interval[start of "Measurement Period" + 4 months, start of "Measurement Period" + 8 months )

define "September through December of Measurement Period":
	Interval[start of "Measurement Period" + 8 months, 
	end of "Measurement Period"]

define "Numerator 1":
	exists ( ["Assessment, Performed": "PHQ 9 and PHQ 9M Tools"] DepressionAssessment
			where DepressionAssessment.result is not null
				and DepressionAssessment.authorDatetime in "September through December of Measurement Period"
	)

define "Numerator 2":
	exists ( ["Assessment, Performed": "PHQ 9 and PHQ 9M Tools"] DepressionAssessment
			where DepressionAssessment.result is not null
				and DepressionAssessment.authorDatetime in "May through August of Measurement Period"
	)

define "Numerator 3":
	exists ( ["Assessment, Performed": "PHQ 9 and PHQ 9M Tools"] DepressionAssessment
			where DepressionAssessment.result is not null
				and DepressionAssessment.authorDatetime in "January through April of Measurement Period"
	)

define "Long Term Care Encounter Overlaps January through April":
	["Encounter, Performed": "Care Services in Long-Term Residential Facility"] EncounterLongTermCare
		where EncounterLongTermCare.relevantPeriod overlaps "January through April of Measurement Period"

define "Long Term Care Encounter Overlaps May through August":
	["Encounter, Performed": "Care Services in Long-Term Residential Facility"] EncounterLongTermCare
		where EncounterLongTermCare.relevantPeriod overlaps "May through August of Measurement Period"

define "Long Term Care Encounter Overlaps September through December":
	["Encounter, Performed": "Care Services in Long-Term Residential Facility"] EncounterLongTermCare
		where EncounterLongTermCare.relevantPeriod overlaps "September through December of Measurement Period"

define "Denominator Exclusion 1":
	exists ( "Palliative Care Order Before End of December" )
		or exists ( "Encounter Palliative Care Overlaps September through December" )
		or exists ( "Long Term Care Encounter Overlaps September through December" )
		or exists ( "Expired in September through December" )
		or exists ( "Disorder Diagnoses Overlaps Depression Encounter in September through December" )

define "Denominator Exclusion 2":
	exists ( "Palliative Care Order Before End of August" )
		or exists ( "Encounter Palliative Care Overlaps May through August" )
		or exists ( "Long Term Care Encounter Overlaps May through August" )
		or exists ( "Expired in May through August" )
		or exists ( "Disorder Diagnoses Overlaps Depression Encounter in May through August" )

define "Denominator Exclusion 3":
	exists ( "Palliative Care Order Before End of April" )
		or exists ( "Encounter Palliative Care Overlaps January through April" )
		or exists ( "Long Term Care Encounter Overlaps January through April" )
		or exists ( "Expired in January through April" )
		or exists ( "Disorder Diagnoses Overlaps Depression Encounter in January through April" )

define "Expired in January through April":
	["Patient Characteristic Expired": "Dead (finding)"] Deceased
		where Deceased.expiredDatetime in "January through April of Measurement Period"

define "Expired in May through August":
	["Patient Characteristic Expired": "Dead (finding)"] Deceased
		where Deceased.expiredDatetime in "May through August of Measurement Period"

define "Expired in September through December":
	["Patient Characteristic Expired": "Dead (finding)"] Deceased
		where Deceased.expiredDatetime in "September through December of Measurement Period"

define "Stratification 1_1":
	["Patient Characteristic Birthdate": "Birth date"] BirthDate
		with "Depression Encounter in September through December" DepressionEncounter
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 12
				and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)< 18

define "Stratification 1_2":
	["Patient Characteristic Birthdate": "Birth date"] BirthDate
		with "Depression Encounter in September through December" DepressionEncounter
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 18

define "Stratification 2_1":
	["Patient Characteristic Birthdate": "Birth date"] BirthDate
		with "Depression Encounter in May through August" DepressionEncounter
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 12
				and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)< 18

define "Stratification 2_2":
	["Patient Characteristic Birthdate": "Birth date"] BirthDate
		with "Depression Encounter in May through August" DepressionEncounter
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 18

define "Stratification 3_2":
	["Patient Characteristic Birthdate": "Birth date"] BirthDate
		with "Depression Encounter in January through April" DepressionEncounter
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 18

define "Depression Encounter in January through April":
	["Encounter, Performed": "Contact or Office Visit"] DepressionEncounter
		with "Depression Diagnoses" Depression
			such that DepressionEncounter.relevantPeriod overlaps Depression.prevalencePeriod
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 12
		where DepressionEncounter.relevantPeriod ends during "Measurement Period"
			and 
			end of DepressionEncounter.relevantPeriod in "January through April of Measurement Period"

define "Depression Encounter in May through August":
	["Encounter, Performed": "Contact or Office Visit"] DepressionEncounter
		with "Depression Diagnoses" Depression
			such that DepressionEncounter.relevantPeriod overlaps Depression.prevalencePeriod
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 12
		where DepressionEncounter.relevantPeriod ends during "Measurement Period"
			and 
			end of DepressionEncounter.relevantPeriod in "May through August of Measurement Period"

define "Depression Encounter in September through December":
	["Encounter, Performed": "Contact or Office Visit"] DepressionEncounter
		with "Depression Diagnoses" Depression
			such that DepressionEncounter.relevantPeriod overlaps Depression.prevalencePeriod
		with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 12
		where DepressionEncounter.relevantPeriod ends during "Measurement Period"
			and 
			end of DepressionEncounter.relevantPeriod in "September through December of Measurement Period"

define "Stratification 3_1":
	["Patient Characteristic Birthdate": "Birth date"] BirthDate
		with "Depression Encounter in January through April" DepressionEncounter
			such that Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)>= 12
				and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of DepressionEncounter.relevantPeriod)< 18