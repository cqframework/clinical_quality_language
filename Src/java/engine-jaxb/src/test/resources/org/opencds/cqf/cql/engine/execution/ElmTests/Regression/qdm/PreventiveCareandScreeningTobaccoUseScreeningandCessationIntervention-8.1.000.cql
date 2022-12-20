library PreventiveCareandScreeningTobaccoUseScreeningandCessationIntervention version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Health & Behavioral Assessment - Individual": 'urn:oid:2.16.840.1.113883.3.526.3.1020'
valueset "Health and Behavioral Assessment - Initial": 'urn:oid:2.16.840.1.113883.3.526.3.1245'
valueset "Health and Behavioral Assessment, Reassessment": 'urn:oid:2.16.840.1.113883.3.526.3.1529'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Limited Life Expectancy": 'urn:oid:2.16.840.1.113883.3.526.3.1259'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Occupational Therapy Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1011'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Ophthalmological Services": 'urn:oid:2.16.840.1.113883.3.526.3.1285'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services - Group Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1027'
valueset "Preventive Care Services - Other": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1030'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Psych Visit - Diagnostic Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1492'
valueset "Psych Visit - Psychotherapy": 'urn:oid:2.16.840.1.113883.3.526.3.1496'
valueset "Psychoanalysis": 'urn:oid:2.16.840.1.113883.3.526.3.1141'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Speech and Hearing Evaluation": 'urn:oid:2.16.840.1.113883.3.526.3.1530'
valueset "Tobacco Non-User": 'urn:oid:2.16.840.1.113883.3.526.3.1189'
valueset "Tobacco Use Cessation Counseling": 'urn:oid:2.16.840.1.113883.3.526.3.509'
valueset "Tobacco Use Cessation Pharmacotherapy": 'urn:oid:2.16.840.1.113883.3.526.3.1190'
valueset "Tobacco Use Screening": 'urn:oid:2.16.840.1.113883.3.526.3.1278'
valueset "Tobacco User": 'urn:oid:2.16.840.1.113883.3.526.3.1170'

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

define "Office Based Visit During Measurement Period":
	( ["Encounter, Performed": "Health & Behavioral Assessment - Individual"]
		union ["Encounter, Performed": "Health and Behavioral Assessment - Initial"]
		union ["Encounter, Performed": "Health and Behavioral Assessment, Reassessment"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Occupational Therapy Evaluation"]
		union ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Ophthalmological Services"]
		union ["Encounter, Performed": "Psych Visit - Diagnostic Evaluation"]
		union ["Encounter, Performed": "Psych Visit - Psychotherapy"]
		union ["Encounter, Performed": "Psychoanalysis"]
		union ["Encounter, Performed": "Speech and Hearing Evaluation"] ) OfficeBasedEncounter
		where OfficeBasedEncounter.relevantPeriod during "Measurement Period"

define "Preventive Visit During Measurement Period":
	( ["Encounter, Performed": "Annual Wellness Visit"]
		union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care Services - Group Counseling"]
		union ["Encounter, Performed": "Preventive Care Services - Other"]
		union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
		union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"] ) PreventiveEncounter
		where PreventiveEncounter.relevantPeriod during "Measurement Period"

define "Screened and Identified as Tobacco User":
	( Last(["Assessment, Performed": "Tobacco Use Screening"] TobaccoUseScreening
			where TobaccoUseScreening.authorDatetime 24 months or less on or before 
			end of "Measurement Period"
			sort by authorDatetime
	)) MostRecentTobaccoUseScreening
		where MostRecentTobaccoUseScreening.result in "Tobacco User"

define "Screened and Identified as Tobacco Non User":
	( Last(["Assessment, Performed": "Tobacco Use Screening"] TobaccoUseScreening
			where TobaccoUseScreening.authorDatetime 24 months or less on or before 
			end of "Measurement Period"
			sort by authorDatetime
	)) MostRecentTobaccoUseScreening
		where MostRecentTobaccoUseScreening.result in "Tobacco Non-User"

define "Denominator 1":
	"Initial Population"

define "Denominator 2":
	"Initial Population"
		and "Screened and Identified as Tobacco User" is not null

define "Denominator 3":
	"Initial Population"

define "Has Limited Life Expectancy":
	["Diagnosis": "Limited Life Expectancy"] LimitedLifeExpectancy
		where LimitedLifeExpectancy.prevalencePeriod overlaps after "Measurement Period"

define "Denominator Exceptions 1":
	exists "Medical Reason for Not Screening for Tobacco Use"
		or exists "Has Limited Life Expectancy"

define "Denominator Exceptions 2":
	exists "Medical Reason for Not Counseling Tobacco User"
		or exists "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy"
		or exists "Has Limited Life Expectancy"

define "Denominator Exceptions 3":
	exists "Medical Reason for Not Screening for Tobacco Use"
		or exists "Medical Reason for Not Counseling Tobacco User"
		or exists "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy"
		or exists "Has Limited Life Expectancy"

define "Tobacco User Received Cessation Counseling":
	["Intervention, Performed": "Tobacco Use Cessation Counseling"] TobaccoCessationCounseling
		with "Screened and Identified as Tobacco User" ScreenedTobaccoUser
			such that TobaccoCessationCounseling.relevantPeriod starts same or after ScreenedTobaccoUser.authorDatetime
				and TobaccoCessationCounseling.relevantPeriod starts before 
				end of "Measurement Period"

define "Numerator 1":
	"Screened and Identified as Tobacco Non User" is not null
		or "Screened and Identified as Tobacco User" is not null

define "Numerator 2":
	exists "Tobacco User Received Cessation Counseling"
		or exists "Tobacco Cessation Pharmacotherapy Ordered for Tobacco User"
		or exists "Tobacco Cessation Pharmacotherapy Actively Being Taken by Tobacco User"

define "Numerator 3":
	"Screened and Identified as Tobacco Non User" is not null
		or ( "Screened and Identified as Tobacco User" is not null
				and ( exists "Tobacco User Received Cessation Counseling"
						or exists "Tobacco Cessation Pharmacotherapy Ordered for Tobacco User"
						or exists "Tobacco Cessation Pharmacotherapy Actively Being Taken by Tobacco User"
				)
		)

define "Tobacco Cessation Pharmacotherapy Actively Being Taken by Tobacco User":
	["Medication, Active": "Tobacco Use Cessation Pharmacotherapy"] TakingPharmacotherapy
		with "Screened and Identified as Tobacco User" ScreenedTobaccoUser
			such that ScreenedTobaccoUser.authorDatetime during TakingPharmacotherapy.relevantPeriod
				and TakingPharmacotherapy.relevantPeriod starts before 
				end of "Measurement Period"

define "Tobacco Cessation Pharmacotherapy Ordered for Tobacco User":
	["Medication, Order": "Tobacco Use Cessation Pharmacotherapy"] PharmacotherapyOrdered
		with "Screened and Identified as Tobacco User" ScreenedTobaccoUser
			such that PharmacotherapyOrdered.authorDatetime same or after ScreenedTobaccoUser.authorDatetime
				and PharmacotherapyOrdered.authorDatetime before 
				end of "Measurement Period"

define "Medical Reason for Not Counseling Tobacco User":
	["Intervention, Not Performed": "Tobacco Use Cessation Counseling"] NoTobaccoCessationCounseling
		with "Screened and Identified as Tobacco User" ScreenedTobaccoUser
			such that NoTobaccoCessationCounseling.authorDatetime same or after ScreenedTobaccoUser.authorDatetime
				and NoTobaccoCessationCounseling.authorDatetime before 
				end of "Measurement Period"
		where NoTobaccoCessationCounseling.negationRationale in "Medical Reason"

define "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy":
	["Medication, Not Ordered": "Tobacco Use Cessation Pharmacotherapy"] NoPharmacotherapyOrdered
		with "Screened and Identified as Tobacco User" ScreenedTobaccoUser
			such that NoPharmacotherapyOrdered.authorDatetime same or after ScreenedTobaccoUser.authorDatetime
				and NoPharmacotherapyOrdered.authorDatetime before 
				end of "Measurement Period"
		where NoPharmacotherapyOrdered.negationRationale in "Medical Reason"

define "Medical Reason for Not Screening for Tobacco Use":
	["Assessment, Not Performed": "Tobacco Use Screening"] NoTobaccoUseScreening
		where NoTobaccoUseScreening.authorDatetime 24 months or less before 
		end of "Measurement Period"
			and NoTobaccoUseScreening.negationRationale in "Medical Reason"

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and ( Count("Office Based Visit During Measurement Period")>= 2
				or exists "Preventive Visit During Measurement Period"
		)