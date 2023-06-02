library TestCodeRef version '1'

using FHIR

codesystem "SNOMED": 'http://snomed.info/sct'
codesystem "LOINC": 'http://loinc.org'
code "XYZ Code": '185349003' from "SNOMED"
code "ABC Code": '55284-4' from "LOINC"

define CodeRef1: "XYZ Code"
define CodeRef2: "ABC Code"