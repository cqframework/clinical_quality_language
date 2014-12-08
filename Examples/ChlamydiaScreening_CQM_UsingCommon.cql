library CMS153_CQM version '2'

using QUICK

include CMS153_Common version '2' called Common

parameter "Measurement Period" default interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

context Patient

define "In Demographic" =
    AgeInYearsAt(start of "Measurement Period") >= 16
        and AgeInYearsAt(start of "Measurement Period") < 24
        and Patient.gender in Common."Female Administrative Sex"

define "Sexually Active" =
    exists(Common."Conditions Indicating Sexual Activity" C where interval[C.onsetDateTime, C.abatementDateTime] overlaps "Measurement Period")
        or exists(Common."Laboratory Tests Indicating Sexual Activity" O where Last(O.event E where E.status = "completed").date during "Measurement Period")

define "In Initial Population" =
    "In Demographic" and "Sexually Active"

define "In Denominator" =
    true

define "In Numerator" =
    exists (Common."Results Present For Chlamydia Screening" S where S.diagnosticDateTime during "Measurement Period")


