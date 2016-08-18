library ChlamydiaScreening_CQM_UsingCommon version '2'

using QUICK

include ChlamydiaScreening_Common version '2' called Common

parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

context Patient

define "In Demographic":
    AgeInYearsAt(start of MeasurementPeriod) >= 16
        and AgeInYearsAt(start of MeasurementPeriod) < 24
        and "Patient"."gender" in Common."Female Administrative Sex"

define "Sexually Active":
    exists(Common."Conditions Indicating Sexual Activity" C where Interval[C."onsetDateTime", C."abatementDate"] overlaps MeasurementPeriod)
        or exists(Common."Laboratory Tests Indicating Sexual Activity" O where Last(O."event" E where E."status" = 'completed' sort by E."dateTime")."dateTime" during MeasurementPeriod)

define "In Initial Population":
    "In Demographic" and "Sexually Active"

define "In Denominator":
    true

define "In Numerator":
    exists (Common."Results Present For Chlamydia Screening" S where S."issued" during MeasurementPeriod)
