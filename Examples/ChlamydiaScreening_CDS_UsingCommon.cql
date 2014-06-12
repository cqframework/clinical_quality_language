using QLIM

context PATIENT

include "ChlamydiaScreening_Common.cql"

valueset "Reason for not performing Chlamydia Screening" = ValueSet("TBD") // Not used in the measure, need a value set for this

let InDemographic =
    Age() >= 16 and Age() < 24 and Gender = "female"

let SexuallyActive =
    exists (conditionsIndicatingSexualActivity)
    or exists (resultsIndicatingSexualActivity)

let NoScreening =
    not exists (chlamydiaScreening S where S.effectiveTime during interval[today - 1 years, now])
    and not exists ([Procedure, Planned: "Chlamydia Screening"] P where P.effectiveTime >= now)
    and not exists (reasonForNoScreening)

let NeedScreening = InDemographic and SexuallyActive and NoScreening



// Data Model Reference
using CQLM

// Establish PATIENT context
context PATIENT

// Include common library
include "ChlamydiaScreening_Common.cql"

// Define additional value sets for use within this artifact
valueset "Reason for not performing Chlamydia Screening" = ValueSet("TBD")


