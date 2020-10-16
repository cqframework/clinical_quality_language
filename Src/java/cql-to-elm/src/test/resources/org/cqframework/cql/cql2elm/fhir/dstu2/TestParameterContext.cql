library TestParameterContext

using FHIR version '1.0.2'

parameter Patient Patient

context Patient

define TestExpression: birthDate.value < Today()

