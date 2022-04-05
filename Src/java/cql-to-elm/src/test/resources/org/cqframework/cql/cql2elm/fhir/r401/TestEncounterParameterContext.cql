library TestEncounterParameterContext

using FHIR version '4.0.1'

parameter Encounter Encounter

context Encounter

define TestExpression: status.value = 'finished'

