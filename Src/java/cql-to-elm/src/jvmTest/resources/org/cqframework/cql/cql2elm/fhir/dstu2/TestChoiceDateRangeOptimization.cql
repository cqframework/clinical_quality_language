library Query version '1'

using FHIR version '1.0.2'
include FHIRHelpers version '1.0.2' called FHIRHelpers

parameter MeasurementPeriod default Interval[DateTime(2014, 1, 1, 0, 0, 0), DateTime(2014, 12, 31, 23, 59, 59))

context Patient

define DateCondition: [Condition] C where C.onsetDateTime during MeasurementPeriod
define ChoiceTypePeriodCondition: [Condition] C where C.onsetPeriod during MeasurementPeriod