library CMS153_CDS version '2'

using QUICK

include CMS153_Common version '2' as Common

valueset "Reason for not performing Chlamydia Screening" = ValueSet('TBD')

context PATIENT

let InDemographic =
    Age() >= 16 and Age() < 24 and gender in Common."Female Administrative Sex"

let SexuallyActive =
    exists (Common.ConditionsIndicatingSexualActivity)
    or exists (Common.LaboratoryTestsIndicatingSexualActivity)

let NoScreening =
    not exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during interval[today - 1 years, today])
    and not exists ([Procedure, Plan: Common."Chlamydia Screening"] P where P.effectiveTime >= now)
    and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening

