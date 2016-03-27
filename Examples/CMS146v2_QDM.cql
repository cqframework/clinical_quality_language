library CMS146_QDM version '2'

/*
 * This take differs from the original in that it uses a function for the
 * repeated timing logic between the encounter and pharyngitis.
 */

using QDM // version '4.2'

valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis": '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications": '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test": '2.16.840.1.113883.3.464.1003.198.12.1012'

parameter MeasurementPeriod Interval<DateTime>

context Patient

define "InDemographic":
    AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18

define "Pharyngitis":
    ["Diagnosis": "Acute Pharyngitis"] union ["Diagnosis": "Acute Tonsillitis"]

define "Antibiotics":
    ["MedicationOrder": "Antibiotic Medications"] M
       where M."negationRationale" is not null

define function isRelated(P "Diagnosis", E "EncounterPerformed") :
    Interval[P."onsetDatetime", P."abatementDatetime"] includes Interval[E."admissionDatetime", E."dischargeDatetime"]
      or P."onsetDatetime" in Interval[E."admissionDatetime", E."dischargeDatetime"]

define "MeasurementPeriodEncounters":
    ["EncounterPerformed": "Ambulatory/ED Visit"] E
        where "InDemographic"
            and E."negationRationale" is not null
            and Interval[E."admissionDatetime", E."dischargeDatetime"] during MeasurementPeriod

define "PharyngitisEncounters":
    "MeasurementPeriodEncounters" E
        with "Pharyngitis" P such that isRelated(P, E)
        with "Antibiotics" A such that A."signedDatetime" 3 days or less after E."admissionDatetime"

define "PharyngitisWithPriorAntibiotics":
    "Pharyngitis" P
        with "Antibiotics" A such that A."signedDatetime" 30 days or less before P."onsetDatetime"

define "ExcludedEncounters":
    "PharyngitisEncounters" E
        with "PharyngitisWithPriorAntibiotics" P such that isRelated(P, E)

define "StrepTestEncounters":
    "PharyngitisEncounters" E
        with ["LaboratoryTestPerformed": "Group A Streptococcus Test"] T
            such that T."negationRationale" is not null
                and T."result" is not null
                and T."startDatetime" in Interval[E."admissionDatetime" - 3 days, E."dischargeDatetime" + 3 days]

define "IPPCount":
    Count("PharyngitisEncounters")

define "DenominatorCount":
    "IPPCount"

define "DenominatorExclusionsCount":
    Count("ExcludedEncounters")

define "NumeratorCount":
    Count("StrepTestEncounters" except "ExcludedEncounters")
