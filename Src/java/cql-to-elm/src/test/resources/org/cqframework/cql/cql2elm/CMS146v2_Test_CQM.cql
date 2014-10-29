library CMS146 version '2'

using QUICK

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Acute Pharyngitis" = '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis" = '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications" = '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test" = '2.16.840.1.113883.3.464.1003.198.12.1012'

context PATIENT

define InDemographic =
    AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18

define Pharyngitis =
    [Condition: "Acute Pharyngitis"] union [Condition: "Acute Tonsillitis"]

define Antibiotics =
    [MedicationTreatment, Order: "Antibiotic Medications"]

define TargetEncounters =
    [Encounter, Performance: "Ambulatory/ED Visit"] E
        with Pharyngitis P such that P.effectiveTime overlaps after E.performedAtTime
        with Antibiotics A such that A.orderedAtTime starts 3 days or less after start E.performedAtTime
        where E.performedAtTime during MeasurementPeriod

define TargetDiagnoses =
    Pharyngitis P with TargetEncounters E such that P.effectiveTime overlaps after E.performedAtTime

define HasPriorAntibiotics =
    exists (Antibiotics A with TargetDiagnoses D such that A.orderedAtTime starts 30 days or less before start D.effectiveTime)

define HasTargetEncounter =
    exists (TargetEncounters)

define InInitialPopulation =
    InDemographic and HasTargetEncounter

define InDenominator =
    true

define InDenominatorExclusions =
    HasPriorAntibiotics

define InNumerator =
    exists ([SimpleObservation: "Group A Streptococcus Test"] R where R.observedAtTime during MeasurementPeriod and R.result is not null)