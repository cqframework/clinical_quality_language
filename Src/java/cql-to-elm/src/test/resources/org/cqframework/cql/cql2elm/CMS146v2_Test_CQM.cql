library CMS146 version '2'

using QUICK

parameter MeasurementPeriod default interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

valueset "Acute Pharyngitis" = '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis" = '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications" = '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test" = '2.16.840.1.113883.3.464.1003.198.12.1012'

context Patient

define InDemographic =
    AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18

define Pharyngitis =
    [Condition: "Acute Pharyngitis"] union [Condition: "Acute Tonsillitis"]

define Antibiotics =
    [MedicationPrescription: "Antibiotic Medications"]

define TargetEncounters =
    [Encounter: "Ambulatory/ED Visit"] E
        with Pharyngitis P such that interval[P.onsetDateTime, P.abatementDateTime] overlaps after E.period
        with Antibiotics A such that A.orderedOn starts 3 days or less after start of E.period
        where E.period during MeasurementPeriod

define TargetDiagnoses =
    Pharyngitis P with TargetEncounters E such that interval[P.onsetDateTime, P.abatementDateTime] overlaps after E.period

define HasPriorAntibiotics =
    exists (Antibiotics A with TargetDiagnoses D such that A.orderedOn starts 30 days or less before D.onsetDateTime)

define HasTargetEncounter =
    exists (TargetEncounters)

define InInitialPopulation =
    InDemographic and HasTargetEncounter

define InDenominator =
    true

define InDenominatorExclusions =
    HasPriorAntibiotics

define InNumerator =
    exists ([Observation: "Group A Streptococcus Test"] R where R.issued during MeasurementPeriod and R.value is not null)