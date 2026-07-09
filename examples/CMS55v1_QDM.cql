library CMS55 version '1'

/*
	CMS55v1 NQF0495
	Median Time from ED Arrival to ED Departure for Admitted ED Patients

Population criteria

    Initial Patient Population =
        AND: "Occurrence A of Encounter, Performed: Encounter Inpatient (length of stay <= 120 day(s))"
        AND: "Occurrence A of Encounter, Performed: Encounter Inpatient (discharge datetime)" during "Measurement Period"
    Measure Population =
        AND: "Initial Patient Population"
        AND: "Occurrence A of Encounter, Performed: Emergency Department Visit" <= 1 hour(s) ends before start of "Occurrence A of Encounter, Performed: Encounter Inpatient"

Measure observations

    Median of:Time difference of:
        AND: "Occurrence A of Encounter, Performed: Emergency Department Visit (facility location arrival datetime)"
        AND: "Occurrence A of Encounter, Performed: Emergency Department Visit (facility location departure datetime)"

Data criteria (QDM Data Elements)

    "Diagnosis, Active: Psychiatric/Mental Health Patient" using "Psychiatric/Mental Health Patient Grouping Value Set (2.16.840.1.113883.3.117.1.7.1.299)"
    "Encounter, Performed: Emergency Department Visit" using "Emergency Department Visit Grouping Value Set (2.16.840.1.113883.3.117.1.7.1.293)"
    "Encounter, Performed: Encounter Inpatient" using "Encounter Inpatient SNOMED-CT Value Set (2.16.840.1.113883.3.666.5.307)"
    Attribute: "Ordinality: Principal Diagnosis" using "Principal Diagnosis SNOMED-CT Value Set (2.16.840.1.113883.3.117.2.7.1.14)"

Reporting Stratification

    Reporting Stratum 1 =
        AND: "Occurrence A of Encounter, Performed: Emergency Department Visit" <= 1 hour(s) ends before start of "Occurrence A of Encounter, Performed: Encounter Inpatient"
    Reporting Stratum 2 =
        AND NOT: "Diagnosis, Active: Psychiatric/Mental Health Patient (ordinality: 'Principal Diagnosis')" starts during "Occurrence A of Encounter, Performed: Emergency Department Visit"
    Reporting Stratum 3 =
        AND: "Diagnosis, Active: Psychiatric/Mental Health Patient (ordinality: 'Principal Diagnosis')" starts during "Occurrence A of Encounter, Performed: Emergency Department Visit"

Supplemental Data Elements

    "Patient Characteristic Ethnicity: Ethnicity" using "Ethnicity CDC Value Set (2.16.840.1.114222.4.11.837)"
    "Patient Characteristic Payer: Payer" using "Payer Source of Payment Typology Value Set (2.16.840.1.114222.4.11.3591)"
    "Patient Characteristic Race: Race" using "Race CDC Value Set (2.16.840.1.114222.4.11.836)"
    "Patient Characteristic Sex: ONC Administrative Sex" using "ONC Administrative Sex Administrative Sex Value Set (2.16.840.1.113762.1.4.1)"

*/

using QDM version '5.0'

valueset "Inpatient": '2.16.840.1.113883.3.666.5.307'
valueset "Emergency Department Visit": '2.16.840.1.113883.3.117.1.7.1.293'

parameter "Measurement Period" Interval<DateTime>
//parameter "Measurement Period" default Interval[@2014-01-01T00:00:00.0, @2015-01-01T00:00:00.0)

context Patient

define "Inpatient Encounters":
	["Encounter, Performed": "Inpatient"] E
		where E.lengthOfStay <= 120 days
			and E.relevantPeriod ends during "Measurement Period"

define "Emergency Department Encounters":
	["Encounter, Performed": "Emergency Department Visit"] ED
		with "Inpatient Encounters" E such that ED.relevantPeriod ends 1 hour or less before start of E.relevantPeriod

define "Measure Observation":
	"Emergency Department Encounters" E
		where E.relevantPeriod is not null
    return duration in minutes of E.locationPeriod

context Population

define "Measure Score": Median("Measure Observation")
