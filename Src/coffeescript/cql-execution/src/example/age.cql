library AgeAtMP version '1'

using QUICK

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

context Patient

define InDemographic =
    AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18