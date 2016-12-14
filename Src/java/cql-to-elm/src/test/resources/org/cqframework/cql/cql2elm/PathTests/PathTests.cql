library PathTests version '1'

using FHIR version '1.8'

context Patient

define TestPathTraversal: Patient.name.given