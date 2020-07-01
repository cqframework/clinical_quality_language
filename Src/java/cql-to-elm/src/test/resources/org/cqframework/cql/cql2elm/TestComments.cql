// Test Comment
library TestComments

// Test Comment
using FHIR version '4.0.1'

// Test Comment
include FHIRHelpers version '4.0.1'

// Test Comment
codesystem "LOINC": 'http://loinc.org'

// Test Comment
valueset "Acute Pharyngitis": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1011'
// Test Comment
valueset "Acute Tonsillitis": 'urn:oid:2.16.840.1.113883.3.464.1003.102.12.1012'
// Test Comment
valueset "Ambulatory/ED Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Female Administrative Sex": 'urn:oid:2.16.840.1.113883.3.560.100.2'

// Test Comment
code "Blood Pressure": '55284-4' from "LOINC" display 'Blood pressure'
code "Systolic Blood Pressure": '8480-6' from "LOINC" display 'Systolic blood pressure'
code "Diastolic Blood Pressure": '8462-4' from "LOINC" display 'Diastolic blood pressure'

// Test Comment
parameter MeasurementPeriod Interval<DateTime>

// Test Comment
context Patient

/*
@author: Chopin
@description: Defines whether the patient is included in the initial population
*/
define InInitialPopulation:
  AgeInYearsAt(start of MeasurementPeriod) >= 16
    // Test in the middle
    and AgeInYearsAt(start of MeasurementPeriod) < 24
    /* Test multi-line in the middle */
    and Patient.gender in "Female Administrative Sex"

/*
@author: Beethoven
@description: Determines the cumulative duration of a list of intervals
@comment: #This function collapses the input intervals prior to determining the cumulative duration
to ensure overlapping intervals do not contribute multiple times to the result#
 */
define function CumulativeDuration(Intervals List<Interval<DateTime>>):
  Sum((collapse Intervals) X return all duration in days of X)

