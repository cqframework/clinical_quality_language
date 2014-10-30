library CMS153_CQM version '2'

using QUICK

include CMS153_Common version '2' called Common

parameter "Measurement Period" default interval[Date(2013, 1, 1), Date(2014, 1, 1))

context PATIENT

define "In Demographic" =
    AgeInYearsAt(start of "Measurement Period") >= 16
        and AgeInYearsAt(start of "Measurement Period") < 24
        and gender in Common."Female Administrative Sex"

define "Sexually Active" =
    exists(Common."Conditions Indicating Sexual Activity" C where C.effectiveTime overlaps "Measurement Period")
        or exists(Common."Laboratory Tests Indicating Sexual Activity" R where R.effectiveTime during "Measurement Period")

define "In Initial Population" =
    "In Demographic" and "Sexually Active"

define "In Denominator" =
    true

define "In Numerator" =
    exists (Common."Results Present For Chlamydia Screening" S where S.effectiveTime during "Measurement Period")


