library CMS135_QDM

using QDM version '5.0.2'

// Valueset definitions allow local names to be used within the artifact
valueset "Care Services in Long-Term Residential Facility": '...'
valueset "Heart Failure": '...'

// Parameters allow for evaluation-time values to be provided to the artifact
parameter "Measurement Period" default Interval[@2014-01-01T00:00:00.0, @2015-01-01T00:00:00.0)

// Identifiers can include spaces and punctuation to make logic more readable
// Because the result is a set, rather than a criteria definition, the notion of occurrencing is not required
define "Long-Term Residential Facility Encounters":
    ["Encounter, Performed": "Care Services in Long-Term Residential Facility"] E
        // By always specifying the attribute involved, filtering is explicit, rather than implicit in the model
        with ["Diagnosis": "Heart Failure"] D
          such that D.prevalencePeriod overlaps before E.relevantPeriod
        where E.relevantPeriod during "Measurement Period"

define "Relevant Encounters":
    "Long-Term Residential Facility Encounters"
//        union "..."
//        union "..."
