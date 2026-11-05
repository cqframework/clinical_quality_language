library CMS154 version '1'

/*
	CMS154v1 - NQF0069
	Appropriate Treatment for Children with Upper Respiratory Infection (URI)

Population criteria

    Initial Patient Population =
        AND: "Patient Characteristic Birthdate: birth date" >= 3 month(s) starts before start of "Measurement Period"
        AND: "Patient Characteristic Birthdate: birth date" <= 18 year(s) starts before start of "Measurement Period"
        AND:
            OR: "Occurrence A of Diagnosis, Active: Upper Respiratory Infection" starts during
                OR: "Encounter, Performed: Office Visit"
                OR: "Encounter, Performed: Emergency Department Visit"
                OR: "Encounter, Performed: Preventive Care- Initial Office Visit, 0 to 17"
                OR: "Encounter, Performed: Preventive Care - Established Office Visit, 0 to 17"
                OR: "Encounter, Performed: Hospital Observation Care - Initial"
                OR: "Encounter, Performed: Face-to-Face Interaction"
                during "Measurement Period"
            OR:
                OR: "Encounter, Performed: Office Visit"
                OR: "Encounter, Performed: Emergency Department Visit"
                OR: "Encounter, Performed: Preventive Care- Initial Office Visit, 0 to 17"
                OR: "Encounter, Performed: Preventive Care - Established Office Visit, 0 to 17"
                OR: "Encounter, Performed: Hospital Observation Care - Initial"
                OR: "Encounter, Performed: Face-to-Face Interaction"
                during ("Occurrence A of Diagnosis, Active: Upper Respiratory Infection" during "Measurement Period")
    Denominator =
        AND: "Initial Patient Population"
    Denominator Exclusions =
        AND:
            OR: "Diagnosis, Active: Competing Conditions for Respiratory Conditions" <= 3 day(s) starts after start of "Occurrence A of Diagnosis, Active: Upper Respiratory Infection"
            OR: "Medication, Active: Antibiotic Medications" <= 30 day(s) starts before or during "Occurrence A of Diagnosis, Active: Upper Respiratory Infection"
    Numerator =
        AND NOT: "Medication, Order: Antibiotic Medications" <= 3 day(s) starts after start of "Occurrence A of Diagnosis, Active: Upper Respiratory Infection"
    Denominator Exceptions =
        None

Data criteria (QDM Data Elements)

    "Diagnosis, Active: Competing Conditions for Respiratory Conditions" using "Competing Conditions for Respiratory Conditions Grouping Value Set (2.16.840.1.113883.3.464.1003.102.12.1017)"
    "Diagnosis, Active: Upper Respiratory Infection" using "Upper Respiratory Infection Grouping Value Set (2.16.840.1.113883.3.464.1003.102.12.1022)"
    "Encounter, Performed: Emergency Department Visit" using "Emergency Department Visit Grouping Value Set (2.16.840.1.113883.3.464.1003.101.12.1010)"
    "Encounter, Performed: Face-to-Face Interaction" using "Face-to-Face Interaction Grouping Value Set (2.16.840.1.113883.3.464.1003.101.12.1048)"
    "Encounter, Performed: Hospital Observation Care - Initial" using "Hospital Observation Care - Initial Grouping Value Set (2.16.840.1.113883.3.464.1003.101.12.1002)"
    "Encounter, Performed: Office Visit" using "Office Visit Grouping Value Set (2.16.840.1.113883.3.464.1003.101.12.1001)"
    "Encounter, Performed: Preventive Care - Established Office Visit, 0 to 17" using "Preventive Care - Established Office Visit, 0 to 17 Grouping Value Set (2.16.840.1.113883.3.464.1003.101.12.1024)"
    "Encounter, Performed: Preventive Care- Initial Office Visit, 0 to 17" using "Preventive Care- Initial Office Visit, 0 to 17 Grouping Value Set (2.16.840.1.113883.3.464.1003.101.12.1022)"
    "Medication, Active: Antibiotic Medications" using "Antibiotic Medications Grouping Value Set (2.16.840.1.113883.3.464.1003.196.12.1001)"
    "Medication, Order: Antibiotic Medications" using "Antibiotic Medications Grouping Value Set (2.16.840.1.113883.3.464.1003.196.12.1001)"
    "Patient Characteristic Birthdate: birth date" using "birth date LOINC Value Set (2.16.840.1.113883.3.560.100.4)"

Reporting Stratification

    None

Supplemental Data Elements

    "Patient Characteristic Ethnicity: Ethnicity" using "Ethnicity CDC Value Set (2.16.840.1.114222.4.11.837)"
    "Patient Characteristic Payer: Payer" using "Payer Source of Payment Typology Value Set (2.16.840.1.114222.4.11.3591)"
    "Patient Characteristic Race: Race" using "Race CDC Value Set (2.16.840.1.114222.4.11.836)"
    "Patient Characteristic Sex: ONC Administrative Sex" using "ONC Administrative Sex Administrative Sex Value Set (2.16.840.1.113762.1.4.1)"

// This is an episode-of-care measure, meaning that each patient may contribute multiple episodes, rather than just 1...
// Need to make sure we can handle this correctly within CQL

*/

using QUICK

valueset "Competing Conditions for Respiratory Conditions": '2.16.840.1.113883.3.464.1003.102.12.1017'
valueset "Upper Respiratory Infection": '2.16.840.1.113883.3.464.1003.102.12.1022'
valueset "Emergency Department Visit": '2.16.840.1.113883.3.464.1003.101.12.1010'
valueset "Face-to-Face Interaction": '2.16.840.1.113883.3.464.1003.101.12.1048'
valueset "Hospital Observation Care - Initial": '2.16.840.1.113883.3.464.1003.101.12.1002'
valueset "Office Visit": '2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Preventive Care - Established Office Visit, 0 to 17": '2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Preventive Care- Initial Office Visit, 0 to 17": '2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Antibiotic Medications": '2.16.840.1.113883.3.464.1003.196.12.1001'

parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

context Patient

define "InDemographic":
    AgeInMonthsAt(start of MeasurementPeriod) >= 3
        and AgeInYearsAt(start of MeasurementPeriod) <= 18

define "ValidEncounters":
	["Encounter": "Office Visit"]
		union ["Encounter": "Emergency Department Visit"]
		union ["Encounter": "Preventive Care- Initial Office Visit, 0 to 17"]
		union ["Encounter": "Preventive Care - Established Office Visit, 0 to 17"]
		union ["Encounter": "Hospital Observation Care - Initial"]
		union ["Encounter": "Face-to-Face Interaction"]

define "EncountersDuringMeasurementPeriod":
	"ValidEncounters" E where E."period" during MeasurementPeriod

define "URIDiagnosis":
	["Condition": "Upper Respiratory Infection"]

define "ValidDiagnosis":
	"URIDiagnosis" U
		where exists ("EncountersDuringMeasurementPeriod" E where U."onsetDateTime" during E."period")
			or exists ("ValidEncounters" E where Interval[U."onsetDateTime", U."abatementDate"] includes E."period")

define "InitialPopulation":
	"ValidDiagnosis" V
		where "InDemographic"

define "Denominator":
	true

define "Numerator":
	"ValidDiagnosis" V where not exists (["MedicationPrescription": "Antibiotic Medications"] M where M."dateWritten" occurs 3 days or less after V."onsetDateTime")

context Population

define "MeasureScore": (Count("Numerator") / Count("Denominator")) * 100
