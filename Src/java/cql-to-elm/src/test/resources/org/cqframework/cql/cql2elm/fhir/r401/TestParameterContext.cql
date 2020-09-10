library TestParameterContext

using FHIR version '4.0.1'

parameter Patient Patient

context Patient

define TestExpression: birthDate.value < Today()

