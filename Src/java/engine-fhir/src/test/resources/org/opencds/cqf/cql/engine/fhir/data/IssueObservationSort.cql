library IssueObservationSort

using FHIR version '4.0.1'

include "FHIRHelpers" version '4.0.1'

define function "Order by starting date"(observations List<FHIR.Observation>):
    if observations is null then null
    else         
        (observations) observation 
        sort by start of "Interval"() asc

define "Ordered Observations":
  "Order by starting date"([Observation])

define fluent function "Interval"(obs FHIR.Observation):
    case 
        when obs is null or obs.effective is null then 
            null
        when obs.effective is FHIR.dateTime then 
            Interval[date from (obs.effective as FHIR.dateTime), date from (obs.effective as FHIR.dateTime)]
        when obs.effective is FHIR.Period then 
            Interval[date from start of (obs.effective as FHIR.Period), date from end of (obs.effective as FHIR.Period)]
        when obs.effective is FHIR.instant 
            then Interval[date from (obs.effective as FHIR.instant), date from (obs.effective as FHIR.instant)]
        else 
            null
    end