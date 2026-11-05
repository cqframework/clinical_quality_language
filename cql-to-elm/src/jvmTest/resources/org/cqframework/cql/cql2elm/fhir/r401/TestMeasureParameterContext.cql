library TestMeasureParameterContext

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter Measure Measure

context Measure

define TestExpression: extension('http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-populationBasis').exists()

