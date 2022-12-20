library ExclusiveBreastMilkFeeding version '9.5.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Birth Weight": 'urn:oid:2.16.840.1.113762.1.4.1029.194'
valueset "Breast Milk": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.30'
valueset "Dietary Intake Other than Breast Milk": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.27'
valueset "Discharge To Acute Care Facility": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.87'
valueset "Encounter Inpatient": 'urn:oid:2.16.840.1.113883.3.666.5.307'
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

define "Numerator":
  "Single Live Term Newborn Encounter With Newborn Fed Breast Milk Only Since Birth"

define "Denominator Exclusions":
  "Single Live Term Newborn Encounter With Newborn to NICU or Discharge to Acute Care or Other Health Care Facility or Expired"
    union "Single Live Term Newborn Encounter With Length of Stay More Than 120 days"
    union "Single Live Term Newborn Encounter With Galactosemia or Parenteral Nutrition"

define "Single Live Birth Encounter":
  ["Encounter, Performed": "Encounter Inpatient"] InpatientEncounter
    where exists ( InpatientEncounter.diagnoses EncounterDiagnoses
        where EncounterDiagnoses.code in "Single Live Born Newborn Born in Hospital"
    )

define "Initial Population":
  "Single Live Term Newborn Encounter During Measurement Period"

define "Single Live Term Newborn Encounter With Galactosemia or Parenteral Nutrition":
  ( "Single Live Term Newborn Encounter During Measurement Period" QualifyingEncounter
      with ["Procedure, Performed": "Parenteral Nutrition"] ParenteralNutrition
        such that ParenteralNutrition.relevantPeriod starts during QualifyingEncounter.relevantPeriod
  )
    union ( "Single Live Term Newborn Encounter During Measurement Period" QualifyingEncounter
        where exists ( QualifyingEncounter.diagnoses BirthEncounterDiagnoses
            where BirthEncounterDiagnoses.code in "Galactosemia"
        )
    )

define "Single Live Term Newborn Encounter With Length of Stay More Than 120 days":
  "Single Live Term Newborn Encounter During Measurement Period" QualifyingEncounter
    where Global."LengthInDays" ( QualifyingEncounter.relevantPeriod ) > 120

define "Single Live Term Newborn Encounter With Newborn Fed Breast Milk Only Since Birth":
  "Single Live Term Newborn Encounter During Measurement Period" QualifyingEncounter
    with ["Substance, Administered": "Breast Milk"] BreastMilkFeeding
      such that BreastMilkFeeding.relevantPeriod starts during QualifyingEncounter.relevantPeriod
    without ["Substance, Administered": "Dietary Intake Other than Breast Milk"] OtherFeeding
      such that OtherFeeding.relevantPeriod starts during QualifyingEncounter.relevantPeriod

define "Single Live Term Newborn Encounter With Newborn to NICU or Discharge to Acute Care or Other Health Care Facility or Expired":
  "Single Live Term Newborn Encounter During Measurement Period" QualifyingEncounter
    where exists ( QualifyingEncounter.facilityLocations NICU
        where NICU.code in "Neonatal Intensive Care Unit (NICU)"
    )
      or QualifyingEncounter.dischargeDisposition in "Patient Expired"
      or QualifyingEncounter.dischargeDisposition in "Discharge To Acute Care Facility"
      or QualifyingEncounter.dischargeDisposition in "Other Health Care Facility"

define "Single Live Term Newborn Encounter During Measurement Period":
  ( "Single Live Birth Encounter With Gestational Age 37 Weeks or More"
    union "Single Live Birth Encounter With Birth Weight 3000 grams or More Without Gestational Age" ) SingleLiveTermEncounter
    where SingleLiveTermEncounter.relevantPeriod ends during "Measurement Period"

define "Single Live Birth Encounter With Birth Weight 3000 grams or More Without Gestational Age":
  "Single Live Birth Encounter" SingleLiveBirthEncounter
    without ["Assessment, Performed": "Gestational age--at birth"] GestationalAge
      such that GestationalAge.relevantDatetime during SingleLiveBirthEncounter.relevantPeriod
        and GestationalAge.result is not null
    with ["Assessment, Performed": "Birth Weight"] BirthWeight
      such that BirthWeight.relevantDatetime during SingleLiveBirthEncounter.relevantPeriod
        and BirthWeight.result >= 3000 'g'

define "Single Live Birth Encounter With Gestational Age 37 Weeks or More":
  "Single Live Birth Encounter" SingleLiveBirthEncounter
    with ["Assessment, Performed": "Gestational age--at birth"] GestationalAge
      such that GestationalAge.result >= 37 'wk'
        and GestationalAge.relevantDatetime during SingleLiveBirthEncounter.relevantPeriod