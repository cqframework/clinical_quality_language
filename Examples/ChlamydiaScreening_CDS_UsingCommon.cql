library CMS153_CDS version '2'

using QUICK

include CMS153_Common version '2' called Common

valueset "Reason for not performing Chlamydia Screening" = 'TBD'

context PATIENT

define InDemographic =
    AgeInYears() >= 16 and AgeInYears() < 24 and gender in Common."Female Administrative Sex"

define SexuallyActive =
    exists (Common.ConditionsIndicatingSexualActivity)
    or exists (Common.LaboratoryTestsIndicatingSexualActivity)

define NoScreening =
    not exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during interval[Today() - 1 years, Today()])
    and not exists ([Procedure, Plan: Common."Chlamydia Screening"] P where P.effectiveTime >= Now())
    and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

define NeedScreening = InDemographic and SexuallyActive and NoScreening

