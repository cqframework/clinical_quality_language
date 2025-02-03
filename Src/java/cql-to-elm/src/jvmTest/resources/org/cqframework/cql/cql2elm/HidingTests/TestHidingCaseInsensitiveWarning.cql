library TestCaseInsensitiveWarning

using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1'

context Patient

define "Patients" :[Patient]
define "patients": [Patient] P where P.gender = 'male'

define function findPatients() :
    "patients" P where P.birthDate > Date(1980,1,1)