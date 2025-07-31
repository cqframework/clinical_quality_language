/*
@description: This is a testing library used to validate capability of the CQL-to-ELM translator
using the US Core model info.
*/
library TestUSCore

using USCore version '7.0.0'

include hl7.fhir.uv.cql.FHIRHelpers version '4.0.1' called FHIRHelpers

codesystem "Marital Status Codes": 'http://terminology.hl7.org/CodeSystem/v3-MaritalStatus'
codesystem "CDC Race and Ethnicity Codes": 'urn:oid:2.16.840.1.113883.6.238'

valueset "Marital Status": 'http://hl7.org/fhir/ValueSet/marital-status'

code "Marital Status - Married": 'M' from "Marital Status Codes"
code "American Indian or Alaska Native": '1002-5' from "CDC Race and Ethnicity Codes"
code "Alaska Native": '1735-0' from "CDC Race and Ethnicity Codes"

context Patient

define "Test AllergyIntolerance Profile": ["AllergyIntolerance Profile"]
define "Test BMI Profile": ["BMI Profile"]
define "Test Blood Pressure Profile": ["Blood Pressure Profile"]
define "Test Body Height Profile": ["Body Height Profile"]
define "Test Body Temperature Profile": ["Body Temperature Profile"]
define "Test Body Weight Profile": ["Body Weight Profile"]
define "Test CarePlan Profile": ["CarePlan Profile"]
define "Test CareTeam Profile": ["CareTeam Profile"]
define "Test Condition Encounter Diagnosis Profile": ["Condition Encounter Diagnosis Profile"]
define "Test Condition Problems and Health Concerns Profile": ["Condition Problems and Health Concerns Profile"]
define "Test Coverage Profile": ["Coverage Profile"]
define "Test DiagnosticReport Profile for Laboratory Results Reporting": ["DiagnosticReport Profile for Laboratory Results Reporting"]
define "Test DiagnosticReport Profile for Report and Note Exchange": ["DiagnosticReport Profile for Report and Note Exchange"]
define "Test DocumentReference Profile": ["DocumentReference Profile"]
define "Test Encounter Profile": ["Encounter Profile"]
define "Test Goal Profile": ["Goal Profile"]
define "Test Head Circumference Profile": ["Head Circumference Profile"]
define "Test Heart Rate Profile": ["Heart Rate Profile"]
define "Test Immunization Profile": ["Immunization Profile"]
define "Test Implantable Device Profile": ["Implantable Device Profile"]
define "Test Laboratory Result Observation Profile": ["Laboratory Result Observation Profile"]
define "Test Location Profile": ["Location Profile"]
define "Test Medication Profile": ["Medication Profile"]
define "Test MedicationDispense Profile": ["MedicationDispense Profile"]
define "Test MedicationRequest Profile": ["MedicationRequest Profile"]
define "Test Observation Clinical Result Profile": ["Observation Clinical Result Profile"]
define "Test Observation Occupation Profile": ["Observation Occupation Profile"]
define "Test Observation Pregnancy Intent Profile": ["Observation Pregnancy Intent Profile"]
define "Test Observation Pregnancy Status Profile": ["Observation Pregnancy Status Profile"]
define "Test Observation Screening Assessment Profile": ["Observation Screening Assessment Profile"]
define "Test Observation Sexual Orientation Profile": ["Observation Sexual Orientation Profile"]
define "Test Organization Profile": ["Organization Profile"]
define "Test Patient Profile": ["Patient Profile"]
define "Test Pediatric BMI for Age Observation Profile": ["Pediatric BMI for Age Observation Profile"]
define "Test Pediatric Head Occipital Frontal Circumference Percentile Profile": ["Pediatric Head Occipital Frontal Circumference Percentile Profile"]
define "Test Pediatric Weight for Height Observation Profile": ["Pediatric Weight for Height Observation Profile"]
define "Test Practitioner Profile": ["Practitioner Profile"]
define "Test PractitionerRole Profile": ["PractitionerRole Profile"]
define "Test Procedure Profile": ["Procedure Profile"]
define "Test Provenance Profile": ["Provenance Profile"]
define "Test Pulse Oximetry Profile": ["Pulse Oximetry Profile"]
define "Test QuestionnaireResponse Profile": ["QuestionnaireResponse Profile"]
define "Test RelatedPerson Profile": ["RelatedPerson Profile"]
define "Test Respiratory Rate Profile": ["Respiratory Rate Profile"]
define "Test ServiceRequest Profile": ["ServiceRequest Profile"]
define "Test Simple Observation Profile": ["Simple Observation Profile"]
define "Test Smoking Status Observation Profile": ["Smoking Status Observation Profile"]
define "Test Specimen Profile": ["Specimen Profile"]
define "Test Vital Signs Profile": ["Vital Signs Profile"]

define TestPrimitives:
  Patient P
    where P.gender = 'male'
      and P.active is true
      and P.birthDate before Today()
      and P.maritalStatus in "Marital Status"
      and P.maritalStatus ~ "Marital Status - Married"

define TestChoice:
  Patient P
    where P.deceased is false
      or P.deceased before Today()
