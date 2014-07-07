using QUICK

context PATIENT

include Common

valueset "Reason for not performing Chlamydia Screening" = ValueSet('TBD')

let InDemographic =
    Age() >= 16 and Age() < 24 and Gender in Common."Female Administrative Sex"

let SexuallyActive =
    exists (Common.ConditionsIndicatingSexualActivity)
    or exists (Common.ResultsIndicatingSexualActivity)

let NoScreening =
    not exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during interval[today - 1 years, today])
    and not exists ([Procedure, Plan: Common."Chlamydia Screening"] P where P.effectiveTime >= now)
    and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening

