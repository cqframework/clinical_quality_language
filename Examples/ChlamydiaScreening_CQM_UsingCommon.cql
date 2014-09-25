library CMS153_CQM version '2'

using QUICK

include CMS153_Common version '2' as Common

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

context PATIENT

define InDemographic =
    AgeAt(start of MeasurementPeriod) >= 16
        and AgeAt(start of MeasurementPeriod) < 24
        and gender in Common."Female Administrative Sex"

define SexuallyActive =
    exists(Common.ConditionsIndicatingSexualActivity C where C.effectiveTime overlaps MeasurementPeriod)
        or exists(Common.LaboratoryTestsIndicatingSexualActivity R where R.effectiveTime during MeasurementPeriod)

define InInitialPopulation =
    InDemographic and SexuallyActive

define InDenominator =
    true

define InNumerator =
    exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during MeasurementPeriod)


