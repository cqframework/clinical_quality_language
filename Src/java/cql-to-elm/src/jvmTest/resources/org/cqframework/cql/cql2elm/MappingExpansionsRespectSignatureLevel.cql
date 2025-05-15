library MappingExpansionsRespectSignatureLevel

// https://github.com/cqframework/clinical_quality_language/issues/1475

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'

parameter "Measurement Period" Interval<DateTime> default Interval[@2024-01-01T00:00:00.0Z, @2025-01-01T00:00:00.0Z)

context Patient

define EncounterPeriod:
  [Encounter] E
    return E.period