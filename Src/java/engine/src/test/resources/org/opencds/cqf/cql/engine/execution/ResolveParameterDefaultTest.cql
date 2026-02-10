library ResolveParameterDefaultTest

parameter "Measurement Period" Interval<DateTime> default Interval[@2020-01-01T00:00:00.000, @2020-12-31T23:59:59.999]
parameter "No Default Param" Integer
parameter "Integer Default" Integer default 42
parameter "Now Default" DateTime default Now()

define "Simple Expression":
  42
