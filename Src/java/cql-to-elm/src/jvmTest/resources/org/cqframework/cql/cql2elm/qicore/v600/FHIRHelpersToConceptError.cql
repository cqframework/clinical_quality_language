library FHIRHelpersToConceptError version '0.2.000'

using QICore version '6.0.0'

include FHIRHelpers version '4.0.1' called FHIRHelpers

parameter "Measurement Period" Interval<DateTime> default Interval[@2025-01-01T00:00:00Z, @2026-01-01T00:00:00Z)

context Patient

define "Minimal Repro":
  [MedicationNotRequested] NoStatinTherapyOrdered
    return NoStatinTherapyOrdered.reasonCode