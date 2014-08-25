library CMS146 version '2'

using QUICK

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Acute Pharyngitis" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')
valueset "Acute Tonsillitis" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1012')
valueset "Ambulatory/ED Visit" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')
valueset "Antibiotic Medications" = ValueSet('2.16.840.1.113883.3.464.1003.196.12.1001')
valueset "Group A Streptococcus Test" = ValueSet('2.16.840.1.113883.3.464.1003.198.12.1012')

context PATIENT

let InDemographic =
    AgeAt(start of MeasurementPeriod) >= 2 and AgeAt(start of MeasurementPeriod) < 18

let Pharyngitis = 
    ([Condition: "Acute Pharyngitis"]) union ([Condition: "Acute Tonsilitis"])

let Antibiotics =
    [MedicationTreatment, Order: "Antibiotic Medications"]

let TargetEncounters =
    [Encounter, Performance: "Ambulatory/ED Visit"] E
        with Pharyngitis P where P.effectiveTime overlaps after E.performedAtTime
        with Antibiotics A where A.orderedAtTime starts at most 3 days after start E.performedAtTime
        where E.performedAtTime during MeasurementPeriod

let TargetDiagnoses =
    Pharyngitis P with TargetEncounters E where P.effectiveTime overlaps after E.performedAtTime

let HasPriorAntibiotics =
    exists (Antibiotics A with TargetDiagnoses D where A.orderedAtTime starts at most 30 days before start D.effectiveTime)

let HasTargetEncounter =
    exists (TargetEncounters)

let InInitialPopulation =
    InDemographic and HasTargetEncounter

let InDenominator =
    true

let InDenominatorExclusions =
    HasPriorAntibiotics

let InNumerator =
    exists ([SimpleObservation: "Group A Streptococcus Test"] R where R.observedAtTime during MeasurementPeriod and R.result is not null)