// @Test: Age
parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))

context Patient

define Age:
  AgeInYearsAt(start of MeasurementPeriod)

define InDemographic:
  AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18  

context Population

define AgeSum: Sum(Age)

define DEMO: Count(InDemographic w where w is true )

define AgeSumRef : AgeSum

