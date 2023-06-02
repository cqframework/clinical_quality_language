library StatinTherapyforthePreventionandTreatmentofCardiovascularDisease version '3.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Annual Wellness Visit": 'urn:oid:2.16.840.1.113883.3.526.2.1363'
valueset "Atherosclerosis and Peripheral Arterial Disease": 'urn:oid:2.16.840.1.113762.1.4.1047.21'
valueset "Breastfeeding": 'urn:oid:2.16.840.1.113762.1.4.1047.73'
valueset "CABG Surgeries": 'urn:oid:2.16.840.1.113883.3.666.5.694'
valueset "Carotid Intervention": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.204'
valueset "Cerebrovascular disease, Stroke, TIA": 'urn:oid:2.16.840.1.113762.1.4.1047.44'
valueset "Diabetes": 'urn:oid:2.16.840.1.113883.3.464.1003.103.12.1001'
valueset "End Stage Renal Disease": 'urn:oid:2.16.840.1.113883.3.526.3.353'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Hepatitis A": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1024'
valueset "Hepatitis B": 'urn:oid:2.16.840.1.113883.3.67.1.101.1.269'
valueset "High intensity statin therapy": 'urn:oid:2.16.840.1.113762.1.4.1047.97'
valueset "Hypercholesterolemia": 'urn:oid:2.16.840.1.113762.1.4.1047.100'
valueset "Ischemic heart disease or coronary occlusion, rupture, or thrombosis": 'urn:oid:2.16.840.1.113762.1.4.1047.46'
valueset "LDL-c": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.215'
valueset "Liver Disease": 'urn:oid:2.16.840.1.113762.1.4.1047.42'
valueset "Low intensity statin therapy": 'urn:oid:2.16.840.1.113762.1.4.1047.107'
valueset "Moderate intensity statin therapy": 'urn:oid:2.16.840.1.113762.1.4.1047.98'
valueset "Myocardial Infarction": 'urn:oid:2.16.840.1.113883.3.526.3.403'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1005'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1040'
valueset "Outpatient Encounters for Preventive Care": 'urn:oid:2.16.840.1.113762.1.4.1047.9'
valueset "Palliative care encounter": 'urn:oid:2.16.840.1.113883.3.600.1.1575'
valueset "Palliative or Hospice Care": 'urn:oid:2.16.840.1.113883.3.600.1.1579'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "PCI": 'urn:oid:2.16.840.1.113762.1.4.1045.67'
valueset "Pregnancy Dx": 'urn:oid:2.16.840.1.113883.3.600.1.1623'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1125'
valueset "Preventive Care Services - Other": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1030'
valueset "Preventive Care Services-Individual Counseling": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.11.1115'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Rhabdomyolysis": 'urn:oid:2.16.840.1.113762.1.4.1047.102'
valueset "Stable and Unstable Angina": 'urn:oid:2.16.840.1.113762.1.4.1047.47'
valueset "Statin Allergen": 'urn:oid:2.16.840.1.113762.1.4.1110.42'

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

define "History of Clinical ASCVD Diagnoses":
	["Diagnosis": "Myocardial Infarction"]
		union ["Diagnosis": "Cerebrovascular disease, Stroke, TIA"]
		union ["Diagnosis": "Atherosclerosis and Peripheral Arterial Disease"]
		union ["Diagnosis": "Ischemic heart disease or coronary occlusion, rupture, or thrombosis"]
		union ["Diagnosis": "Stable and Unstable Angina"]

define "History of Clinical ASCVD Procedures":
	["Procedure, Performed": "PCI"]
		union ["Procedure, Performed": "CABG Surgeries"]
		union ["Procedure, Performed": "Carotid Intervention"]

define "Active Statin Use":
	["Medication, Active": "Low intensity statin therapy"]
		union ["Medication, Active": "Moderate intensity statin therapy"]
		union ["Medication, Active": "High intensity statin therapy"]

define "Statin Therapy Starts Before End of Measurement Period":
	"Active Statin Use" StatinActive
		where StatinActive.relevantPeriod overlaps after "Measurement Period"

define "Qualifying Encounters":
	["Encounter, Performed": "Annual Wellness Visit"]
		union ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Outpatient Encounters for Preventive Care"]
		union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
		union ["Encounter, Performed": "Preventive Care Services - Other"]
		union ["Encounter, Performed": "Preventive Care Services-Individual Counseling"]
		union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]

define "Encounters During Measurement Period":
	"Qualifying Encounters" ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Palliative Care Encounter Performed":
	["Encounter, Performed": "Palliative care encounter"] PalliativeCareEncounter
		where PalliativeCareEncounter.relevantPeriod starts on or before 
		end of "Measurement Period"

define "ASCVD Procedure Before End of Measurement Period":
	"History of Clinical ASCVD Procedures" PriorASCVDProcedure
		where PriorASCVDProcedure.relevantPeriod starts before 
		end of "Measurement Period"

define "ASCVD Diagnosis Before End of Measurement Period":
	"History of Clinical ASCVD Diagnoses" PriorASCVDdiagnosis
		where PriorASCVDdiagnosis.prevalencePeriod starts before 
		end of "Measurement Period"

define "History of ASCVD Diagnosis or Procedure":
	"ASCVD Diagnosis Before End of Measurement Period"
		union "ASCVD Procedure Before End of Measurement Period"

define "Denominator 1":
	"Initial Population"
		and exists "History of ASCVD Diagnosis or Procedure"

define "Most Recent LDL":
	Last("LDL Test Result In Past 2 Years")

define "Last LDL Less Than 70":
	"Most Recent LDL" RecentLDL
		where RecentLDL.result < 70 'mg/dL'

define "Pregnant At Any Time During the Measurement Period":
	["Diagnosis": "Pregnancy Dx"] Pregnancy
		where Pregnancy.prevalencePeriod overlaps "Measurement Period"

define "Statin Ordered":
	["Medication, Order": "Low intensity statin therapy"]
		union ["Medication, Order": "Moderate intensity statin therapy"]
		union ["Medication, Order": "High intensity statin therapy"]

define "Statin Ordered During Measurement Period":
	"Statin Ordered" StatinOrder
		where StatinOrder.authorDatetime during "Measurement Period"

define "Breastfeeding At Any Time During the Measurement Period":
	["Diagnosis": "Breastfeeding"] Breastfeed
		where Breastfeed.prevalencePeriod overlaps "Measurement Period"

define "Has ESRD":
	exists ( ["Diagnosis": "End Stage Renal Disease"] ESRD
			where ESRD.prevalencePeriod overlaps "Measurement Period"
	)

define "Has HepatitisA":
	exists ( ["Diagnosis": "Hepatitis A"] HepA
			where HepA.prevalencePeriod overlaps "Measurement Period"
	)

define "Has HepatitisB":
	exists ( ["Diagnosis": "Hepatitis B"] HepB
			where HepB.prevalencePeriod overlaps "Measurement Period"
	)

define "Has Hypercholesterolemia":
	exists ( ["Diagnosis": "Hypercholesterolemia"] Hypercholesterolemia
			where Hypercholesterolemia.prevalencePeriod starts before 
			end of "Measurement Period"
	)

define "Has Liver Disease":
	exists ( ["Diagnosis": "Liver Disease"] LiverDisease
			where LiverDisease.prevalencePeriod overlaps "Measurement Period"
	)

define "Has Rhabdomyolysis":
	exists ( ["Diagnosis": "Rhabdomyolysis"] Rhabdomyolysis
			where Rhabdomyolysis.prevalencePeriod overlaps "Measurement Period"
	)

define "Denominator 2":
	"Initial Population"
		and ( exists "LDL Greater Than or Equal to 190 in Patient History"
				or "Has Hypercholesterolemia"
		)
		and not exists "History of ASCVD Diagnosis or Procedure"

define "Denominator 3":
	"Initial Population"
		and "Has Diabetes"
		and exists "LDL 70 to 189 During or 2 Years Before Start of Measurement Period"
		and "Patient Age 40 to 75 at Start of Measurement Period"
		and not exists "History of ASCVD Diagnosis or Procedure"
		and not exists "LDL Greater Than or Equal to 190 in Patient History"
		and not "Has Hypercholesterolemia"

define "Denominator Exclusions":
	exists "Pregnant At Any Time During the Measurement Period"
		or exists "Breastfeeding At Any Time During the Measurement Period"
		or "Has Rhabdomyolysis"

define "Initial Population":
	"Patients Age 21 or Over at Start of Measurement Period"
		and exists "Encounters During Measurement Period"

define "Numerator":
	exists "Statin Ordered During Measurement Period"
		or exists "Statin Therapy Starts Before End of Measurement Period"

define "LDL Between 70 and 189":
	["Laboratory Test, Performed": "LDL-c"] LDL
		where LDL.result < 190 'mg/dL'
			and LDL.result >= 70 'mg/dL'

define "LDL Greater Than or Equal to 190 in Patient History":
	["Laboratory Test, Performed": "LDL-c"] LDL
		where LDL.result >= 190 'mg/dL'
			and LDL.relevantPeriod starts before 
			end of "Measurement Period"

define "LDL Test Result In Past 2 Years":
	["Laboratory Test, Performed": "LDL-c"] LDL
		where LDL.relevantPeriod starts 3 years or less before 
		end "Measurement Period"
			and LDL.result is not null
		sort by start of relevantPeriod

define "Palliative or Hospice Care Ordered":
	["Intervention, Order": "Palliative or Hospice Care"] PalliativeCareOrder
		where PalliativeCareOrder.authorDatetime on or before 
		end of "Measurement Period"

define "Palliative or Hospice Care Order or Encounter":
	"Palliative or Hospice Care Ordered"
		union "Palliative Care Encounter Performed"

define "Denominator Exceptions":
	exists "Palliative or Hospice Care Order or Encounter"
		or "Has ESRD"
		or "Has HepatitisA"
		or "Has HepatitisB"
		or "Has Liver Disease"
		or exists "Adverse Reaction to Statins"
		or exists "Allergy or Intolerance to Statins"

define "Denominator Exceptions Population 3":
	exists "Palliative or Hospice Care Order or Encounter"
		or "Has ESRD"
		or "Has HepatitisA"
		or "Has HepatitisB"
		or "Has Liver Disease"
		or exists "Adverse Reaction to Statins"
		or exists "Allergy or Intolerance to Statins"
		or "Last LDL Less Than 70" is not null

define "Has Diabetes":
	exists ( ["Diagnosis": "Diabetes"] Diabetes
			where Diabetes.prevalencePeriod overlaps "Measurement Period"
	)

define "LDL 70 to 189 During or 2 Years Before Start of Measurement Period":
	"LDL Between 70 and 189" LDLRange
		where LDLRange.relevantPeriod starts 3 years or less before 
		end "Measurement Period"

define "Adverse Reaction to Statins":
	["Adverse Event": "Statin Allergen"] StatinReaction
		where StatinReaction.relevantPeriod overlaps "Measurement Period"

define "Allergy or Intolerance to Statins":
	["Allergy/Intolerance": "Statin Allergen"] StatinAllergyIntolerance
		where StatinAllergyIntolerance.prevalencePeriod overlaps "Measurement Period"

define "Patient Age 40 to 75 at Start of Measurement Period":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 40
			and Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")<= 75

define "Patients Age 21 or Over at Start of Measurement Period":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 21