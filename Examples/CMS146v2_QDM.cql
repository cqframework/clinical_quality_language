library CMS146_QDM version '2'

/*
 * This take differs from the original in that it uses a function for the
 * repeated timing logic between the encounter and pharyngitis.
 */

using QDM

valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis": '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications": '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test": '2.16.840.1.113883.3.464.1003.198.12.1012'

parameter MeasurementPeriod Interval<DateTime>

context Patient

define InDemographic:
    AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18

define Pharyngitis:
    ["Diagnosis, Active": "Acute Pharyngitis"] union ["Diagnosis, Active": "Acute Tonsillitis"]

define Antibiotics:
    ["Medication, Order": "Antibiotic Medications"]

define function isRelated(P DiagnosisActive, E EncounterPerformed) {
    return Interval[P."start datetime", P."stop datetime"] includes Interval[E."start datetime", E."stop datetime"]
    or P."start datetime" in Interval[E."start datetime", E."stop datetime"]
}

define MeasurementPeriodEncounters:
    ["Encounter, Performed": "Ambulatory/ED Visit"] E
        where InDemographic
            and Interval[E."start datetime", E."stop datetime"] during MeasurementPeriod

define PharyngitisEncounters:
    MeasurementPeriodEncounters E
        with Pharyngitis P such that isRelated(P, E)
        with Antibiotics A such that A."signed datetime" 3 days or less after E."start datetime"

define PharyngitisWithPriorAntibiotics:
    Pharyngitis P
        with Antibiotics A such that A."signed datetime" 30 days or less before P."start datetime"

define ExcludedEncounters:
    PharyngitisEncounters E
        with PharyngitisWithPriorAntibiotics P such that isRelated(P, E)

define StrepTestEncounters:
    PharyngitisEncounters E
        with ["Laboratory Test, Performed": "Group A Streptococcus Test"] T
            such that T.result is not null
                and T."start datetime" in Interval[E."start datetime" - 3 days, E."stop datetime" + 3 days]

define IPPCount:
    Count(PharyngitisEncounters)

define DenominatorCount:
    IPPCount

define DenominatorExclusionsCount:
    Count(ExcludedEncounters)

define NumeratorCount:
    Count(StrepTestEncounters except ExcludedEncounters)
