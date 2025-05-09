library Issue1504 version '2.0.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Initial Population":