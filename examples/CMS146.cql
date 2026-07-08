library CMS146 version '3'

using QDM // version '4.2'

include Common version '1' called Common

valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis": '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications": '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test": '2.16.840.1.113883.3.464.1003.198.12.1012'

parameter MeasurementPeriod Interval<DateTime>

context Patient

// Region: Initial Population

define "In Demographic":
  AgeInYearsAt(start of MeasurementPeriod) >= 2
  and AgeInYearsAt(start of MeasurementPeriod) < 18

define "Antibiotics":
  ["Medication, Order": "Antibiotic Medications"] M
     where M.negationRationale is not null

define "Pharyngitis":
  ["Diagnosis": "Acute Pharyngitis"] union ["Diagnosis": "Acute Tonsillitis"]

define "Measurement Period Encounters":
  ["Encounter, Performed": "Ambulatory/ED Visit"] E
    where E.negationRationale is not null
      and Interval[E.admissionDatetime, E.dischargeDatetime] during MeasurementPeriod
      and "In Demographic"

define "Pharyngitis Encounters With Antibiotics":
  "Measurement Period Encounters" E
    with "Pharyngitis" P such that Common.includesOrStartsDuring(P, E)
    with "Antibiotics" A such that A.signedDatetime 3 days or less after E.admissionDatetime

define "Initial Population":
  "Pharyngitis Encounters With Antibiotics"

// EndRegion: Initial Population

// Region: Denominator

// None

// EndRegion: Denominator

// Region: Denominator Exclusions

define "Pharyngitis With Prior Antibiotics":
  "Pharyngitis" P
    with "Antibiotics" A such that A."signedDatetime" 30 days or less before P."onsetDatetime"

define "Excluded Encounters":
  "Pharyngitis Encounters With Antibiotics" E
    with "Pharyngitis With Prior Antibiotics" P such that Common.includesOrStartsDuring(P, E)

define "DenominatorExclusions":
  "Excluded Encounters"

// EndRegion: Denominator Exclusions

// Region: Denominator Exceptions

// None

// EndRegion: Denominator Exceptions

// Region: Numerator

define "Strep Test Encounters":
  "Pharyngitis Encounters With Antibiotics" E
    with ["Laboratory Test, Performed": "Group A Streptococcus Test"] T
      such that T.result is not null
        and T.negationRationale is not null
        and T.startDatetime in Interval[E.admissionDatetime - 3 days, E.dischargeDatetime + 3 days]

define "Numerator":
  "Strep Test Encounters"

// EndRegion: Numerator

// Region: Numerator Exclusion

// None

// EndRegion: Numerator Exclusion
