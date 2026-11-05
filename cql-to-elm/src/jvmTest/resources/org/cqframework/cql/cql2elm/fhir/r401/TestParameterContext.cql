library TestParameterContext

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter Patient Patient

context Patient

define TestExpression: birthDate.value < Today()

define TestExtensionExpression: extension('http://hl7.org/fhir/StructureDefinition/birth-time').exists()

