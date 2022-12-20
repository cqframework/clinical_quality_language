library ExclusiveBreastMilkFeeding version '8.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Breast Milk": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.30'
valueset "Dietary Intake Other than Breast Milk": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.27'
valueset "Discharge To Acute Care Facility": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.87'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Galactosemia": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.35'
valueset "Neonatal Intensive Care Unit (NICU)": 'urn:oid:2.16.840.1.113762.1.4.1029.205'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Other Health Care Facility": 'urn:oid:2.16.840.1.113762.1.4.1029.67'
valueset "Parenteral Nutrition": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.38'
valueset "Patient Expired": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.309'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Single Live Born Newborn Born in Hospital": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.26'

code "Gestational age--at birth": '76516-4' from "LOINC" display 'Gestational age--at birth'

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Initial Population":
	"Single Live Birth Encounter With Gestational Age 37 Weeks or More"

define "Denominator":
	"Initial Population"

define "Single Live Birth Encounter With Newborn Fed Breast Milk Only Since Birth":
	"Single Live Birth Encounter With Gestational Age 37 Weeks or More" QualifyingEncounter
		with ["Substance, Administered": "Breast Milk"] BreastMilkFeeding
			such that BreastMilkFeeding.relevantPeriod starts during QualifyingEncounter.relevantPeriod
		without ["Substance, Administered": "Dietary Intake Other than Breast Milk"] OtherFeeding
			such that OtherFeeding.relevantPeriod starts during QualifyingEncounter.relevantPeriod

define "Single Live Birth Encounter With Newborn to NICU or Discharge to Acute Care or Other Health Care Facility or Expired":
	"Single Live Birth Encounter With Gestational Age 37 Weeks or More" QualifyingEncounter
		where exists ( QualifyingEncounter.facilityLocations NICU
				where NICU.code in "Neonatal Intensive Care Unit (NICU)"
		)
			or QualifyingEncounter.dischargeDisposition in "Patient Expired"
			or QualifyingEncounter.dischargeDisposition in "Discharge To Acute Care Facility"
			or QualifyingEncounter.dischargeDisposition in "Other Health Care Facility"

define "Single Live Birth Encounter Without Galactosemia and Parenteral Nutrition":
	"Single Live Birth Encounter" SingleLiveBirthEncounter
		without ["Procedure, Performed": "Parenteral Nutrition"] ParenteralNutrition
			such that ParenteralNutrition.relevantPeriod starts during SingleLiveBirthEncounter.relevantPeriod
		where not exists ( SingleLiveBirthEncounter.diagnoses BirthEncounterDiagnoses
				where BirthEncounterDiagnoses in "Galactosemia"
		)

define "Single Live Birth Encounter With Gestational Age 37 Weeks or More":
	"Single Live Birth Encounter Without Galactosemia and Parenteral Nutrition" QualifyingSingleNewbornEncounter
		with ["Assessment, Performed": "Gestational age--at birth"] GestationalAge
			such that GestationalAge.authorDatetime during QualifyingSingleNewbornEncounter.relevantPeriod
				and GestationalAge.result >= 37 weeks

define "Numerator":
	"Single Live Birth Encounter With Newborn Fed Breast Milk Only Since Birth"

define "Denominator Exclusions":
	"Single Live Birth Encounter With Newborn to NICU or Discharge to Acute Care or Other Health Care Facility or Expired"

define "Single Live Birth Encounter":
	Global."Inpatient Encounter" InpatientEncounter
		where exists ( InpatientEncounter.diagnoses EncounterDiagnoses
				where EncounterDiagnoses in "Single Live Born Newborn Born in Hospital"
		)