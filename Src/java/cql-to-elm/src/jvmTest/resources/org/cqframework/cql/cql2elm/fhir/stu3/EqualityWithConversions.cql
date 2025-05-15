library Query version '1'
using FHIR version '3.0.0'

include FHIRHelpers version '3.0.0' called FHIRHelpers

context Patient

//define Invalid: 5 = '5'

define GetGender: Patient.gender = 'female'
