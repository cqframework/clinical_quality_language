library CMS146_QDM version '2'

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

define Pharyngitis =
    ["Diagnosis, Active": "Acute Pharyngitis"] union ["Diagnosis, Active": "Acute Tonsillitis"]

define Antibiotics =
    ["Medication, Order": "Antibiotic Medications"]

define PharyngitisEncounters =
    ["Encounter, Performed": "Ambulatory/ED Visit"] E
        with Pharyngitis P such that
            interval[P."start datetime", P."stop datetime"] includes interval[E."start datetime", E."stop datetime"]
            or P."start datetime" in interval[E."start datetime", E."stop datetime"]
        with Antibiotics A such that A."signed datetime" 3 days or less after E."start datetime"
        where interval[E."start datetime", E."stop datetime"] during MeasurementPeriod

define PharyngitisWithPriorAntibiotics =
    Pharyngitis P
        with Antibiotics A such that A."signed datetime" 30 days or less before P."start datetime"

define ExcludedEncounters =
    PharyngitisEncounters E
        with PharyngitisWithPriorAntibiotics P such that
            interval[P."start datetime", P."stop datetime"] includes interval[E."start datetime", E."stop datetime"]
            or P."start datetime" in interval[E."start datetime", E."stop datetime"]

define StrepTestEncounters =
    PharyngitisEncounters E
        with ["Laboratory Test, Performed": "Group A Streptococcus Test"] T
          such that T.result is not null
            and (
              T."start datetime" 3 days before E."start datetime"
              or T."start datetime" 3 days after E."stop datetime"
              or T."start datetime" in interval[E."start datetime", E."stop datetime"]
            )

define IPPCount =
    if InDemographic then Count(PharyngitisEncounters) else 0

define DenominatorCount =
    IPPCount

define DenominatorExclusionsCount =
    if DenominatorCount > 0 then Count(ExcludedEncounters) else 0

define NumeratorCount =
    if DenominatorCount > 0 then Count(StrepTestEncounters except ExcludedEncounters) else 0
