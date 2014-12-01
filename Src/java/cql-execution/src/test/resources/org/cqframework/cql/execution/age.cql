library AgeAtMP version '1'

using QUICK

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

context PATIENT

define InDemographic =
    AgeAt(start of MeasurementPeriod) >= 2 and AgeAt(start of MeasurementPeriod) < 18