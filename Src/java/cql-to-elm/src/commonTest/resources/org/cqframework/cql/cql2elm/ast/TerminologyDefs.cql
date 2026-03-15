library TerminologyDefs version '1.0.0'
using System

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMEDCT": 'http://snomed.info/sct' version '2023-09'

valueset "Blood Pressure": 'http://example.com/vs/blood-pressure'

code "Systolic BP": '8480-6' from "LOINC" display 'Systolic blood pressure'
code "Diastolic BP": '8462-4' from "LOINC" display 'Diastolic blood pressure'

concept "Blood Pressure Codes": { "Systolic BP", "Diastolic BP" } display 'Blood Pressure'

context Patient

define CodeSystemRef: "LOINC"
define ValueSetRef: "Blood Pressure"
define CodeRef: "Systolic BP"
define ConceptRef: "Blood Pressure Codes"
