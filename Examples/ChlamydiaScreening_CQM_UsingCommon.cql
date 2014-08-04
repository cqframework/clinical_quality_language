library CMS153_CQM version '2'

using QUICK

include CMS153_Common version '2' as Common

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

context PATIENT

let InDemographic =
    AgeAt(start of MeasurementPeriod) >= 16
        and AgeAt(start of MeasurementPeriod) < 24
        and gender in Common."Female Administrative Sex"

let SexuallyActive =
    exists(Common.ConditionsIndicatingSexualActivity C where C.effectiveTime overlaps MeasurementPeriod)
        or exists(Common.LaboratoryTestsIndicatingSexualActivity R where R.effectiveTime during MeasurementPeriod)

let InInitialPopulation =
    InDemographic and SexuallyActive

let InDenominator =
    true

let InNumerator =
    exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during MeasurementPeriod)


