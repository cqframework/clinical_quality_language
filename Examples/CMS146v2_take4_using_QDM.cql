library CMS146_QDM version '2'

/*
 * This take differs from take3 in that it constructs a tuple with explicit
 * flags indicating what population each encounter falls into.
 */

using QDM

valueset "Acute Pharyngitis" = '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis" = '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications" = '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test" = '2.16.840.1.113883.3.464.1003.198.12.1012'

parameter MeasurementPeriod default interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

context Patient

define InDemographic =
    AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18

define EncounterData =
    foreach
        ["Encounter, Performed": "Ambulatory/ED Visit"] E,
        (["Diagnosis, Active": "Acute Pharyngitis"] union ["Diagnosis, Active": "Acute Tonsillitis"]) P,
        ["Medication, Order": "Antibiotic Medications"] A,
        ["Laboratory Test, Performed": "Group A Streptococcus Test"] T
    define
        InMP = interval[E."start datetime", E."stop datetime"] during MeasurementPeriod,
        PharyngitisDx = interval[P."start datetime", P."stop datetime"] includes interval[E."start datetime", E."stop datetime"]
            or P."start datetime" in interval[E."start datetime", E."stop datetime"],
        PrescribedAntibiotics = A."signed datetime" 3 days or less after E."start datetime",
        PriorAntibiotics = A."signed datetime" 30 days or less before P."start datetime",
        StrepTest = T.result is not null
            and (
              T."start datetime" 3 days before E."start datetime"
              or T."start datetime" 3 days after E."stop datetime"
              or T."start datetime" in interval[E."start datetime", E."stop datetime"]
            )
    where
        InDemographic and InMP and PharyngitisDx and PrescribedAntibiotics
    return {
        Encounter: E,
        InDenominatorExclusions: PriorAntibiotics,
        InNumerator: StrepTest and PriorAntibiotics is not true
    }

define IPPCount =
    Count (distinct EncounterData E return E.Encounter)

define DenominatorCount =
    IPPCount

define DenominatorExclusionsCount =
    Count (distinct EncounterData E where E.InDenominatorExclusions is true return E.Encounter)

define NumeratorCount =
    Count (distinct EncounterData E where E.InNumerator is true return E.Encounter)
