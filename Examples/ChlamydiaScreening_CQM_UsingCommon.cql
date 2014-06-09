using QLIM

context PATIENT

include "ChlamydiaScreening_Common.cql"

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

let InDemographic =
    AgeAt(start of MeasurementPeriod) >= 16
        and AgeAt(start of MeasurementPeriod) < 24
        and Gender = "female"

let SexuallyActive =
    exists(conditionsIndicatingSexualActivity C where C.effectiveTime overlaps before MeasurementPeriod)
        or exists(resultsIndicatingSexualActivity R where R.effectiveTime during MeasurementPeriod)

let InInitialPopulation =
    InDemographic and SexuallyActive

let InDenominator =
    InInitialPopulation

let InNumerator =
    InDenominator and exists (chlamydiaScreening S where S.effectiveTime during MeasurementPeriod)


