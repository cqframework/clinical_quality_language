library CMS9 version '4'

using QUICK // version '1'

valueset "From Patient to Provider: Feeding Intention-Breast": '2.16.840.1.113762.1.4.1045.29'
valueset "Galactosemia": '2.16.840.1.113883.3.117.1.7.1.35'
valueset "Single Live Birth": '2.16.840.1.113883.3.117.1.7.1.25)'
valueset "Single Live Born Newborn Born in Hospital": '2.16.840.1.113883.3.117.1.7.1.26'
valueset "Encounter Inpatient": '2.16.840.1.113883.3.666.5.307'
valueset "Estimated Gestational Age at Birth": '2.16.840.1.113762.1.4.1045.47'
valueset "Parenteral Nutrition": '2.16.840.1.113883.3.117.1.7.1.38'
valueset "Breast Milk": '2.16.840.1.113883.3.117.1.7.1.30'
valueset "Dietary Intake Other than Breast Milk": '2.16.840.1.113883.3.117.1.7.1.27'
valueset "Neonatal Intensive Care Unit (NICU)": '2.16.840.1.113883.3.117.1.7.1.75'
valueset "Patient Expired": '2.16.840.1.113883.3.117.1.7.1.309'
valueset "Discharge To Acute Care Facility": '2.16.840.1.113883.3.117.1.7.1.87'

valueset "Ethnicity CDCREC": '2.16.840.1.114222.4.11.837'
valueset "Payer SOP": '2.16.840.1.114222.4.11.3591'
valueset "Race CDCREC": '2.16.840.1.114222.4.11.836'
valueset "ONC Administrative Sex AdministrativeGender": '2.16.840.1.113762.1.4.1'

parameter MeasurementPeriod Interval<DateTime>

context Patient

define "EncounterInpatient":
  ["Encounter": "Encounter Inpatient"] E
    where duration in days of E."period" <= 120
      and end of E."period" in MeasurementPeriod

define "Gestational Age at Birth <= 37 weeks":
  ["Observation": "Gestational Age At Birth"] O
    where (singleton from O."component")."valueQuantity" >= 259 days // 37 weeks

define "Single Live Birth":
  ["Condition": "Single Live Birth"]
    union ["Condition": "Single Live Born Newborn Born in Hospital"]

define "Parenteral Nutrition":
  ["Procedure": "Parenteral Nutrition"]

define "Galactosemia Diagnosis":
  ["Condition": "Galactosemia"]

define "Live Birth Infants without Parenteral Nutrition":
  "EncounterInpatient" E
    with "Gestational Age at Birth <= 37 weeks" G such that G."effectiveDateTime" in E."period"
    with "Single Live Birth" B such that B."onsetDateTime" in E."period"
    without "Galactosemia" D such that D."onsetDateTime" in E."period"
    without "Parenteral Nutrition" P such that P."performedDateTime" in E."period"

// Region: Initial Population 1

define "InitialPopulation1":
  "Live Birth Infants without Parenteral Nutrition"

// EndRegion: Initial Population 1

// Region: Denominator 1

define "Denominator1":
  "InitialPopulation1"

// EndRegion: Denominator 1

// Region: Denominator Exclusions 1

define "DenominatorExclusions1":
  "EncounterInpatient" E
    where exists (E."Location" L where L."type" in "Neonatal Intensive Care Unit (NICU)")
      or exists (E."hospitalization" H where H."dischargeDisposition" in "Patient Expired")
      or exists (E."hospitalization" H where H."dischargeDisposition" in "Discharge To Acute Care Facility")

// EndRegion: Denominator Exclusions 1

// Region: Numerator 1

// TODO: Are these really "MedicationAdministration"?
// Seems like a stretch but I don't see any other resource in FHIR/QUICK that would be a better fit...
define "Breast Milk Feeding":
  ["MedicationAdministration": "Breast Milk"]

define "Other Than Breast Milk Feeding":
  ["MedicationAdministration": "Dietary Intake Other than Breast Milk"]

define "Numerator1":
  "EncounterInpatient" E
    with "Breast Milk Feeding" F such that F."effectiveDateTime" in E."period"
    without "Other Than Breast Milk Feeding" O such that O."effectiveDateTime" in E."period"

// EndRegion: Numerator 1

// Region: Numerator Exclusions 1
// NONE
// End Region: Numerator Exclusions 1

// Region: Denominator Exceptions 1
// NONE
// End Region: Denominator Exceptions 1

// Region: Stratification 1
// NONE
// End Region: Stratification 1

// Region: Initial Population 2

define "InitialPopulation2":
  "Live Birth Infants without Parenteral Nutrition"

// EndRegion: Initial Population 2

// Region: Denominator 2

// EndRegion: Denominator 2

// Region: Denominator Exclusions 2

// TODO: How do we indicate that this is the infant's birth date, not the mother's?
define "Infant Birth Date":
  ["Patient Characteristic": "Birthdate"]

// TODO: In the QDM, this is expressed as the "absence of an intention to breast feed",
// but this is not the same as the intended "presence of intention not to breast feed"
define "Intention Not To Breast Feed":
  "EncounterInpatient" E
    without ["Communication": "Feeding Intention-Breast"] C
      such that C."sender" is Patient
        and exists (C."recipient" R where R is Practitioner)
        and C."sent" <= 1 hour after start of "Infant Birth Date"

define "DenominatorExclusions2":
  "DenominatorExclusions1"
    union "Intention Not To Breast Feed"

// EndRegion: Denominator Exclusions 2

// Region: Numerator 2

define "Numerator2":
  "Numerator1"

// EndRegion: Numerator 2

// Region: Numerator Exclusions 2
// NONE
// End Region: Numerator Exclusions 2

// Region: Denominator Exceptions 2
// NONE
// End Region: Denominator Exceptions 2

// Region: Stratification 2
// NONE
// End Region: Stratification 2


/*
// TODO: How do we represent Supplemental Data Elements in the CQL?
Supplemental Data Elements

    "Patient Characteristic Ethnicity: Ethnicity" using "Ethnicity CDCREC Value Set (2.16.840.1.114222.4.11.837)"
    "Patient Characteristic Payer: Payer" using "Payer SOP Value Set (2.16.840.1.114222.4.11.3591)"
    "Patient Characteristic Race: Race" using "Race CDCREC Value Set (2.16.840.1.114222.4.11.836)"
    "Patient Characteristic Sex: ONC Administrative Sex" using "ONC Administrative Sex AdministrativeGender Value Set (2.16.840.1.113762.1.4.1)"

*/
