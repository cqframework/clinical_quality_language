library CMS146_QDM_MS version '2'

/*
 * This take differs from the original in that it uses one multi-source query
 * instead of multiple single-source queries.
 */

using QDM

valueset "Acute Pharyngitis" : '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis" : '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit" : '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications" : '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test" : '2.16.840.1.113883.3.464.1003.198.12.1012'

parameter MeasurementPeriod Interval<DateTime>

context Patient

define InDemographic :
    AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18

define EncounterData :
    from
        ["Encounter, Performed": "Ambulatory/ED Visit"] E,
        (["Diagnosis, Active": "Acute Pharyngitis"] union ["Diagnosis, Active": "Acute Tonsillitis"]) P,
        ["Medication, Order": "Antibiotic Medications"] A,
        ["Laboratory Test, Performed": "Group A Streptococcus Test"] T
    define
        InMP : Interval[E."start datetime", E."stop datetime"] during MeasurementPeriod,
        PharyngitisDx : Interval[P."start datetime", P."stop datetime"] includes Interval[E."start datetime", E."stop datetime"]
            or P."start datetime" in Interval[E."start datetime", E."stop datetime"],
        PrescribedAntibiotics : A."signed datetime" 3 days or less after E."start datetime"
    where
        InDemographic and InMP and PharyngitisDx and PrescribedAntibiotics
    return {
        Encounter: E,
        PriorAntibiotics: A."signed datetime" 30 days or less before P."start datetime",
        StrepTest: T.result is not null
            and T."start datetime" in Interval[E."start datetime" - 3 days, E."stop datetime" + 3 days]
    }

define IPPCount : Count (
    EncounterData E return E.Encounter
)

define DenominatorCount : IPPCount

define DenominatorExclusionsCount : Count (
    EncounterData E
        where E.PriorAntibiotics
        return E.Encounter
)

define NumeratorCount : Count (
    EncounterData E
        where E.StrepTest
            and (not E.PriorAntibiotics or E.PriorAntibiotics is null)
        return E.Encounter
)
