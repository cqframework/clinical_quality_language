library ChlamydiaScreening_CQM_UsingCommon version '2'

using QUICK

include ChlamydiaScreening_Common version '2' called Common

parameter Measurement Period default Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

context Patient

define "$In Demographic":
    AgeInYearsAt(start of Measurement Period) >= 16
        and AgeInYearsAt(start of Measurement Period) < 24
        and "Patient"."gender" in Common."$Female Administrative Sex"

define "$Sexually Active":
    exists(Common."$Conditions Indicating Sexual Activity" C where Interval[C."onsetDateTime", C."abatementDate"] overlaps Measurement Period)
        or exists(Common."$Laboratory Tests Indicating Sexual Activity" O where Last(O."event" E where E."status" = 'completed' sort by E."dateTime")."dateTime" during Measurement Period)

define "$In Initial Population":
    "$In Demographic" and "$Sexually Active"

define "$In Denominator":
    true

define "$In Numerator":
    exists (Common."$Results Present For Chlamydia Screening" S where S."issued" during Measurement Period)
