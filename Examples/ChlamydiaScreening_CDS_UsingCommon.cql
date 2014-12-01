library CMS153_CDS version '2'

using QUICK

include CMS153_Common version '2' called Common

valueset "Reason for not performing Chlamydia Screening" = 'TBD'

context Patient

define "In Demographic" =
    AgeInYears() >= 16 and AgeInYears() < 24 and Patient.gender in Common."Female Administrative Sex"

define "Sexually Active" =
    exists (Common."Conditions Indicating Sexual Activity")
    or exists (Common."Laboratory Tests Indicating Sexual Activity")

define "No Screening" =
    not exists (Common."Results Present For Chlamydia Screening" S where S.diagnosticDateTime during interval[Today() - 1 years, Today()])
    and not exists ([ProcedureRequest: Common."Chlamydia Screening"] P where P.requestDateTime same day or after Today()
    and not exists ([Observation: "Reason for not performing Chlamydia Screening"])

define "Need Screening" = "In Demographic" and "Sexually Active" and "No Screening"

