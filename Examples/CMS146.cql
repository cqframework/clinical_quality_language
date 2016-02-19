library CMS146 version '3'

using QDM

include Common version '1' called Common

valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis": '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications": '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test": '2.16.840.1.113883.3.464.1003.198.12.1012'

parameter MeasurementPeriod Interval<DateTime>

context Patient

// Region: Initial Population

define InDemographic:
  AgeInYearsAt(start of MeasurementPeriod) >= 2
  and AgeInYearsAt(start of MeasurementPeriod) < 18

define Antibiotics:
  ["Medication, Order": "Antibiotic Medications"]

define Pharyngitis:
  ["Diagnosis, Active": "Acute Pharyngitis"] union ["Diagnosis, Active": "Acute Tonsillitis"]

define MeasurementPeriodEncounters:
  ["Encounter, Performed": "Ambulatory/ED Visit"] E
    where Interval[E."start datetime", E."stop datetime"] during MeasurementPeriod
      and InDemographic

define PharyngitisEncountersWithAntibiotics:
  MeasurementPeriodEncounters E
    with Pharyngitis P such that Common.includesOrStartsDuring(P, E)
    with Antibiotics A such that A."signed datetime" 3 days or less after E."start datetime"

define InitialPopulation:
  PharyngitisEncountersWithAntibiotics

// EndRegion: Initial Population

// Region: Denominator

// None

// EndRegion: Denominator

// Region: Denominator Exclusions

define PharyngitisWithPriorAntibiotics:
  Pharyngitis P
    with Antibiotics A such that A."signed datetime" 30 days or less before P."start datetime"

define ExcludedEncounters:
  PharyngitisEncountersWithAntibiotics E
    with PharyngitisWithPriorAntibiotics P such that Common.includesOrStartsDuring(P, E)

define DenominatorExclusions:
  ExcludedEncounters

// EndRegion: Denominator Exclusions

// Region: Denominator Exceptions

// None

// EndRegion: Denominator Exceptions

// Region: Numerator

define StrepTestEncounters:
  PharyngitisEncountersWithAntibiotics E
    with ["Laboratory Test, Performed": "Group A Streptococcus Test"] T
      such that T.result is not null
        and T."start datetime" in Interval[E."start datetime" - 3 days, E."stop datetime" + 3 days]

define Numerator:
  StrepTestEncounters

// EndRegion: Numerator

// Region: Numerator Exclusion

// None

// EndRegion: Numerator Exclusion
