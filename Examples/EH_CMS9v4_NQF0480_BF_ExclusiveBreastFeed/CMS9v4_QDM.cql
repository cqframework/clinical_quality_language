library CMS9 version '4'

using QDM //version '4.2'

valueset "Feeding Intention-Breast": '2.16.840.1.113762.1.4.1045.29'
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

define "$Inpatient Encounter":
  ["Encounter, Performed": "Encounter Inpatient"] E
    where E.negationRationale is not null
      and days between E.admissionDatetime and E.dischargeDatetime <= 120
      and E.dischargeDatetime in MeasurementPeriod

define "$Gestational Age at Birth <= 37 weeks":
  ["Physical Exam, Performed": "Gestational Age At Birth"] O
    where O.negationRationale is not null
       and O.result >= 259 days // 37 weeks
    // NOTE: Had to add quantitativeResult to QDM to get proper type resolution here

define "$Live Birth":
  ["Diagnosis": "Single Live Birth"] // NOTE: QDM 4.2 only has Diagnosis, not Diagnosis, Active
    union ["Diagnosis": "Single Live Born Newborn Born in Hospital"]

define "$Galactosemia Diagnosis":
  ["Diagnosis": "Galactosemia"]

define "$Parenteral Nutrition Procedure":
  ["Procedure, Performed": "Parenteral Nutrition"] P
     where P."negationRationale" is not null

define "$Live Birth Infants without Parenteral Nutrition":
  "$Inpatient Encounter" E
    with "$Gestational Age at Birth <= 37 weeks" G such that G.startDatetime between E.admissionDatetime and E.dischargeDatetime
    with "$Live Birth" B such that B.onsetDatetime between E.admissionDatetime and E.dischargeDatetime
    without "$Galactosemia Diagnosis" D such that D.onsetDatetime between E.admissionDatetime and E.dischargeDatetime
    without "$Parenteral Nutrition Procedure" P such that P.startDatetime between E.admissionDatetime and E.dischargeDatetime

// Region: Initial Population 1

define "$Initial Population 1":
  "$Live Birth Infants without Parenteral Nutrition"

// EndRegion: Initial Population 1

// Region: Denominator 1

define "$Denominator 1":
  "$Initial Population 1"

// EndRegion: Denominator 1

// Region: Denominator Exclusions 1

define "$Denominator Exclusions 1":
  "$Inpatient Encounter" E
    where E.facilityLocation in "Neonatal Intensive Care Unit (NICU)"
      or E.dischargeStatus in "Patient Expired"
      or E.dischargeStatus in "Discharge To Acute Care Facility"

// EndRegion: Denominator Exclusions 1

// Region: Numerator 1

define "$Breast Milk Feeding":
  ["Substance, Administered": "Breast Milk"] S
     where S.negationRationale is not null

define "$Other Than Breast Milk Feeding":
  ["Substance, Administered": "Dietary Intake Other than Breast Milk"] S
     where S.negationRationale is not null

define "$Numerator 1":
  "$Inpatient Encounter" E
    with "$Breast Milk Feeding" F such that F.startDatetime between E.admissionDatetime and E.dischargeDatetime
    without "$Other Than Breast Milk Feeding" O such that O.startDatetime between E.admissionDatetime and E.dischargeDatetime

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

define "$Initial Population 2":
  "$Live Birth Infants without Parenteral Nutrition"

// EndRegion: Initial Population 2

// Region: Denominator 2

// EndRegion: Denominator 2

// Region: Denominator Exclusions 2

// TODO: How do we indicate that this is the infant's birth date, not the mother's?
define "$Infant Birth Date":
  (singleton from (["Patient Characteristic Birthdate"])).startDatetime

// TODO: In the QDM, this is expressed as the "absence of an intention to breast feed",
// but this is not the same as the intended "presence of intention not to breast feed"

define "$Intention Not To Breast Feed":
  "$Inpatient Encounter" E
    without ["Communication: From Patient to Provider": "Feeding Intention-Breast"] C
      such that C.startDatetime <= "$Infant Birth Date" + 1 hour // NOTE: Would be nice to be able to say 1 hour after "Infant Birth Date"...

//The above is interesting from the perspective of "negationRationale".
//The above retrieves ALL Inpatient Encounters that have NO documented Communications
//NOR is there a record of negation for Communication.
//Thus, these instances are those in which the DR presumably forgot to ask about Breast Feeding.

define "$Denominator Exclusions 2":
  "$Denominator Exclusions 1"
    union "$Intention Not To Breast Feed"

// EndRegion: Denominator Exclusions 2

// Region: Numerator 2

define "Numerator2":
  "$Numerator 1"

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

