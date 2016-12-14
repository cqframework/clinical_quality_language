library PathTests version '1'

using FHIR version '1.8'

context Patient

define TestPathTraversal: Patient.name.given
define TestFirst: Patient.name.first()
define TestWhere: Patient.name.where(use.value = 'official')
