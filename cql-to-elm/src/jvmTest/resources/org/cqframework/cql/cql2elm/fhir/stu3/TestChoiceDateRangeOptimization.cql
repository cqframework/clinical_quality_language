library Query version '1'

using FHIR version '3.0.0'
include FHIRHelpers version '3.0.0' called FHIRHelpers

parameter MeasurementPeriod default Interval[DateTime(2014, 1, 1, 0, 0, 0), DateTime(2014, 12, 31, 23, 59, 59))

context Patient

define DateCondition: [Condition] C where C.assertedDate during MeasurementPeriod
define ChoiceTypePeriodCondition: [Condition] C where C.onset as FHIR.Period during MeasurementPeriod