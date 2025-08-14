library mCODE version '1'

using FHIR version '4.0.0'
include FHIRHelpers version '4.0.0' called FHIRHelpers

codesystem "LOINC": 'http://loinc.org'

code "ECOG performance code": '89262-0' from "LOINC" display 'ECOG Performance Status [Interpretation]'
code "Karnofsky performance code": '89245-5' from "LOINC" display 'Karnofsky Performance Status [Interpretation]'

concept "ECOG performance score" : { "ECOG performance code" }
concept "Karnofsky performance score" : { "Karnofsky performance code" }

define TestCodeComparison:
  [Observation] O
    where O.code ~ "ECOG performance code"

define TestConceptComparison:
  [Observation] O
    where O.code ~ "ECOG performance score"

