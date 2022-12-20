library PreventiveCareandScreeningInfluenzaImmunization version '9.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Allergy to Eggs": 'urn:oid:2.16.840.1.113883.3.526.3.1253'
valueset "Allergy to Influenza Vaccine": 'urn:oid:2.16.840.1.113883.3.526.3.1256'
valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.3.1240'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Discharge Services - Nursing Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1013'
valueset "Egg Substance": 'urn:oid:2.16.840.1.113883.3.526.3.1537'
valueset "Encounter-Influenza": 'urn:oid:2.16.840.1.113883.3.526.3.1252'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Hemodialysis": 'urn:oid:2.16.840.1.113883.3.526.3.1083'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Influenza Vaccination": 'urn:oid:2.16.840.1.113883.3.526.3.402'
valueset "Influenza Vaccination Declined": 'urn:oid:2.16.840.1.113883.3.526.3.1255'
valueset "Influenza Vaccine": 'urn:oid:2.16.840.1.113883.3.526.3.1254'
valueset "Intolerance to Influenza Vaccine": 'urn:oid:2.16.840.1.113883.3.526.3.1257'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Patient Provider Interaction": 'urn:oid:2.16.840.1.113883.3.526.3.1012'
valueset "Patient Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1008'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Peritoneal Dialysis": 'urn:oid:2.16.840.1.113883.3.526.3.1084'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services - Group Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1027'
valueset "Preventive Care Services - Other": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1030'
valueset "Preventive Care Services, Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Preventive Care, Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Previous Receipt of Influenza Vaccine": 'urn:oid:2.16.840.1.113883.3.526.3.1185'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "System Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1009'

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

define "Egg Substance Allergy":
	["Allergy/Intolerance": "Egg Substance"] AllergyEggSubstance
		where AllergyEggSubstance.prevalencePeriod overlaps after "Influenza Season"

define "Initial Qualifying Encounter During Measurement Period":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Patient Provider Interaction"]
		union ["Encounter, Performed": "Preventive Care Services, Initial Office Visit, 0 to 17"]
		union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
		union ["Encounter, Performed": "Preventive Care Services - Group Counseling"]
		union ["Encounter, Performed": "Preventive Care Services - Other"]
		union ["Encounter, Performed": "Discharge Services - Nursing Facility"]
		union ["Encounter, Performed": "Nursing Facility Visit"]
		union ["Encounter, Performed": "Annual Wellness Visit"]
		union ["Encounter, Performed": "Preventive Care, Established Office Visit, 0 to 17"]
		union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"] ) QualifyingEncounter
		where QualifyingEncounter.relevantPeriod during "Measurement Period"

define "Hemodialysis During Measurement Period":
	["Procedure, Performed": "Hemodialysis"] Hemodialysis
		where Hemodialysis.relevantPeriod during "Measurement Period"

define "Peritoneal Dialysis During Measurement Period":
	["Procedure, Performed": "Peritoneal Dialysis"] PeritonealDialysis
		where PeritonealDialysis.relevantPeriod during "Measurement Period"

define "Peritoneal Dialysis During Influenza Season":
	["Procedure, Performed": "Peritoneal Dialysis"] FluSeasonPeritonealDialysis
		where FluSeasonPeritonealDialysis.relevantPeriod starts during "Influenza Season"

define "Intolerance of Influenza Vaccination Procedure":
	["Allergy/Intolerance": "Influenza Vaccination"] FluVaccinationIntolerance
		where FluVaccinationIntolerance.prevalencePeriod overlaps after "Influenza Season"

define "Influenza Vaccination Procedure":
	["Procedure, Performed": "Influenza Vaccination"] FluVaccination
		where FluVaccination.relevantPeriod starts during "Influenza Season Including August and September of the Prior Year"

define "Influenza Immunization Administered":
	["Immunization, Administered": "Influenza Vaccine"] AdministeredFluVaccine
		where AdministeredFluVaccine.authorDatetime during "Influenza Season Including August and September of the Prior Year"

define "Hemodialysis During Influenza Season":
	["Procedure, Performed": "Hemodialysis"] FluSeasonHemodialysis
		where FluSeasonHemodialysis.relevantPeriod starts during "Influenza Season"

define "Encounter During Influenza Season":
	["Encounter, Performed": "Encounter-Influenza"] FluEncounter
		where FluEncounter.relevantPeriod starts during "Influenza Season"

define "Diagnosis of Intolerance to Influenza Vaccine":
	["Diagnosis": "Intolerance to Influenza Vaccine"] FluVaccineIntoleranceDiagnosis
		where FluVaccineIntoleranceDiagnosis.prevalencePeriod overlaps after "Influenza Season"

define "Allergy or Intolerance to Influenza Vaccine":
	["Allergy/Intolerance": "Influenza Vaccine"] FluVaccineAllergyIntolerance
		where FluVaccineAllergyIntolerance.prevalencePeriod overlaps after "Influenza Season"

define "Denominator":
	"Initial Population"
		and ( exists "Encounter During Influenza Season"
				or exists "Hemodialysis During Influenza Season"
				or exists "Peritoneal Dialysis During Influenza Season"
		)

define "Diagnosis of Allergy to Egg":
	["Diagnosis": "Allergy to Eggs"] EggAllergy
		where EggAllergy.prevalencePeriod overlaps after "Influenza Season"

define "Diagnosis of Allergy to Influenza Vaccine":
	["Diagnosis": "Allergy to Influenza Vaccine"] FluVaccineAllergyDiagnosis
		where FluVaccineAllergyDiagnosis.prevalencePeriod overlaps after "Influenza Season"

define "Medical Patient or System Reason for Not Performing Influenza Vaccination":
	["Procedure, Not Performed": "Influenza Vaccination"] NoFluVaccination
		where NoFluVaccination.authorDatetime during "Influenza Season Including August and September of the Prior Year"
			and ( NoFluVaccination.negationRationale in "Medical Reason"
					or NoFluVaccination.negationRationale in "Patient Reason"
					or NoFluVaccination.negationRationale in "System Reason"
					or NoFluVaccination.negationRationale in "Influenza Vaccination Declined"
			)

define "Denominator Exceptions":
	exists "Medical Patient or System Reason for Not Performing Influenza Vaccination"
		or exists "Medical Patient or System Reason for Not Administering Influenza Vaccine"
		or exists "Diagnosis of Allergy to Egg"
		or exists "Egg Substance Allergy"
		or exists "Diagnosis of Allergy to Influenza Vaccine"
		or exists "Diagnosis of Intolerance to Influenza Vaccine"
		or exists "Intolerance of Influenza Vaccination Procedure"
		or exists "Allergy or Intolerance to Influenza Vaccine"

define "Influenza Season":
	Interval[start of "Measurement Period" - 3 months, start of "Measurement Period" + 3 months )

define "Influenza Season Including August and September of the Prior Year":
	Interval[start of "Measurement Period" - 5 months, start of "Measurement Period" + 3 months )

define "Medical Patient or System Reason for Not Administering Influenza Vaccine":
	["Immunization, Not Administered": "Influenza Vaccine"] NoFluVaccine
		where NoFluVaccine.authorDatetime during "Influenza Season Including August and September of the Prior Year"
			and ( NoFluVaccine.negationRationale in "Medical Reason"
					or NoFluVaccine.negationRationale in "Patient Reason"
					or NoFluVaccine.negationRationale in "System Reason"
					or NoFluVaccine.negationRationale in "Influenza Vaccination Declined"
			)

define "Influenza Vaccine Previously Received":
	["Assessment, Performed": "Previous Receipt of Influenza Vaccine"] PreviousReceiptFluVaccine
		where PreviousReceiptFluVaccine.authorDatetime during "Influenza Season Including August and September of the Prior Year"

define "Numerator":
	exists "Influenza Vaccination Procedure"
		or exists "Influenza Immunization Administered"
		or exists "Influenza Vaccine Previously Received"

define "Initial Population":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInMonthsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 6
	)
		and ( exists "Initial Qualifying Encounter During Measurement Period"
				or exists "Hemodialysis During Measurement Period"
				or exists "Peritoneal Dialysis During Measurement Period"
		)