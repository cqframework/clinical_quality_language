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

define TargetEncounters =
    ["Encounter, Performed": "Ambulatory/ED Visit"] E
        with Pharyngitis P such that
          interval[P."start datetime", P."stop datetime") overlaps after interval[E."start datetime", E."stop datetime")
        with Antibiotics A such that A."signed datetime" 3 days or less after E."start datetime"
        where interval[E."start datetime", E."stop datetime") during MeasurementPeriod

define TargetDiagnoses =
    Pharyngitis P
      with TargetEncounters E such that
        interval[P."start datetime", P."stop datetime") overlaps after interval[E."start datetime", E."stop datetime")

define HasPriorAntibiotics =
    exists (Antibiotics A with TargetDiagnoses D such that A."signed datetime" 30 days or less before D."start datetime")

define HasTargetEncounter =
    exists (TargetEncounters)

define InInitialPopulation =
    InDemographic and HasTargetEncounter

define InDenominator =
    true

define InDenominatorExclusions =
    HasPriorAntibiotics

define InNumerator =
    exists (["Laboratory Test, Performed": "Group A Streptococcus Test"] R where R."start datetime" during MeasurementPeriod and R."result code" is not null)
