library ExistsIssue

using QDM version '5.0.2'

// NOTE: Simplified measure definition for illustration purposes only

valueset "Prostate Cancer": 'urn:oid:2.16.840.1.113883.3.526.3.319'
valueset "Patient Reason Refused": 'urn:oid:2.16.840.1.113883.3.600.1.1503'
valueset "Androgen deprivation therapy for Urology Care": 'urn:oid:2.16.840.1.113762.1.4.1151.48'
valueset "Male": 'urn:oid:2.16.840.1.113883.3.560.100.1'
valueset "Injection Leuprolide Acetate": 'urn:oid:2.16.840.1.113762.1.4.1151.16'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Office Visit Encounter":
  ["Encounter, Performed": "Office Visit"] Encounter
    where Encounter.relevantPeriod during "Measurement Period"

define "Male Patient":
  ["Patient Characteristic": "Male"]

// NOTE: The latest version of the MAT includes support for CQL 1.2, so this comparison:
//define "Prostate Cancer Diagnosis":
//  ["Diagnosis": "Prostate Cancer"] ProstateCancerDx
//    where ProstateCancerDx.prevalencePeriod starts before end "Measurement Period"
//      or ProstateCancerDx.prevalencePeriod starts same as end "Measurement Period"

// Can now be expressed using the "on or before" syntax:
define "Prostate Cancer Diagnosis":
  ["Diagnosis": "Prostate Cancer"] ProstateCancerDx
    where ProstateCancerDx.prevalencePeriod starts on or before end "Measurement Period"

define "Active Medications for Androgen deprivation therapy for Urology Care":
  ["Medication, Active": "Androgen deprivation therapy for Urology Care"] ADT
    with "Prostate Cancer Diagnosis" ProstateCancerDx
      such that ADT.relevantPeriod starts on or after start ProstateCancerDx.prevalencePeriod
    with ["Procedure, Order": "Injection Leuprolide Acetate"] ADTOrder
      such that ADT.relevantPeriod includes ADTOrder.authorDatetime

// NOTE: The latest version of the MAT now correctly identifies some invalid syntax that was previously missed
// by the translator. Specifically, the use of alias references in a sort by clause.

// sort by start of ADT.relevantPeriod

// To fix the issue, remove the alias reference from the sort:

// sort by start of relevantPeriod

define "Androgen Deprivation Therapy":
  {
    First("Active Medications for Androgen deprivation therapy for Urology Care" ADT
        sort by start of relevantPeriod
    )
  }

define "Initial Population":
  exists "Androgen Deprivation Therapy"
    and exists "Male Patient"
    and exists "Office Visit Encounter"

// NOTE: The above approach uses list syntax ({ }) to allow the "Androgen Deprivation Therapy"
// to be used with an "exists". However, in the case that there is no "First" result, the behavior
// of exists is unintuitive (but correct according to the CQL specification):

define Result: exists ({ null })

// This is because from the perspective of the "exists", the list has a single element
// of null, and the exists is evaluated as "if the list has any elements, return true"
// This behavior is unintuitive and we are submitting a comment to CQL to discuss it
// further with the community. However, it can be addressed by changing to a "null test"
// rather than using an "exists":

/*
define "Androgen Deprivation Therapy":
  First("Active Medications for Androgen deprivation therapy for Urology Care" ADT
      sort by start of relevantPeriod
  )

define "Initial Population":
  "Androgen Deprivation Therapy" is not null
    and exists "Male Patient"
    and exists "Office Visit Encounter"
*/

