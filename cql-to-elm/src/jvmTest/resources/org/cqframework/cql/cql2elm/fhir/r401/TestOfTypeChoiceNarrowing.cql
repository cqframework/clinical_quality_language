library TestOfTypeChoiceNarrowing

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

context Patient

// #1785: ofType(dateTime) must narrow the static result type so Exists resolves
// against list<FHIR.dateTime> rather than the un-narrowed choice<...>.
define ObservationOfTypeExists:
  [Observation] O
    where O.effective.ofType(dateTime).exists()

// Same root cause for Count.
define ObservationOfTypeCount:
  [Observation] O
    where O.effective.ofType(dateTime).count() > 0

// Comparison case — compiled even before the fix; must keep compiling after.
define ObservationOfTypeCompare:
  [Observation] O
    where O.effective.ofType(dateTime) >= @2024-01-01

// Exposes the ofType result directly so the narrowed static type can be asserted
// against the emitted ELM (#1785).
define ObservationEffectiveDateTime:
  First([Observation]).effective.ofType(dateTime)
