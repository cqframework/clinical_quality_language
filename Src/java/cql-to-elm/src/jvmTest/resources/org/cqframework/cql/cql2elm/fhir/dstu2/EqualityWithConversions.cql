library Query version '1'
using FHIR version '1.0.2'

include FHIRHelpers version '1.0.2' called FHIRHelpers

context Patient

//define Invalid: 5 = '5'

define GetGender: Patient.gender = 'female'
