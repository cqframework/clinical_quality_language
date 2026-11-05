library TestFHIRTiming

using FHIR version '3.0.0'

include FHIRHelpers version '3.0.0'

parameter "Measurement Period" Interval<DateTime>

context Patient

define Procedures:
		[Procedure] P
      // This translates as an "in" because the timing phrase
      // is looking for whether the expression is not list- or interval-valued
      // Because performed is a "choice", it could be interval-valued, or it could be datetime-valued
      // In that case, it should continue to treat it as an interval timing phrase, not a left-point-valued timing phrase
			// Fixed much more generally with a combination of interval promotion/demotion and better instantiation in the presence of choice types
      where P.performed included in "Measurement Period"
