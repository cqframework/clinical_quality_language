library IssueSortByFluentFunction

using FHIR version '4.0.1'

include "FHIRHelpers" version '4.0.1'

define "Ordered Observations":
 [Observation] O
    sort by start of "Effective"() asc

define fluent function "Effective"(obs FHIR.Observation):
    obs.effective
    