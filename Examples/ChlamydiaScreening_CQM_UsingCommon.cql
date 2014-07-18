library CMS153_CQM version '2'

using QUICK

context PATIENT

include CMS153_Common version '2' as Common

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

let InDemographic =
    AgeAt(start of MeasurementPeriod) >= 16
        and AgeAt(start of MeasurementPeriod) < 24
        and Gender in Common."Female Administrative Sex"

let SexuallyActive =
    exists(Common.ConditionsIndicatingSexualActivity C where C.effectiveTime overlaps before MeasurementPeriod)
        or exists(Common.ResultsIndicatingSexualActivity R where R.effectiveTime during MeasurementPeriod)

let InInitialPopulation =
    InDemographic and SexuallyActive

let InDenominator =
    InInitialPopulation

let InNumerator =
    InDenominator and exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during MeasurementPeriod)


