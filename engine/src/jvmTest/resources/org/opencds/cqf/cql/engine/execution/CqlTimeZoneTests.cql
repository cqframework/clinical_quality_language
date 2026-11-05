library CqlTimeZoneTests

parameter "Measurement Period" Interval<DateTime> default Interval[@2021-01-01T00:00:00.000, @2022-01-01T00:00:00.000)
parameter "Measurement Period 2" Interval<DateTime> default Interval[@2021-07-01T00:00:00.000, @2022-07-01T00:00:00.000)

define "the interval": //Interval[2020-07-01T00:00:00.000, 2021-07-01T00:00:00.000)
Interval[start of "Measurement Period" - 6 months, start of "Measurement Period" + 6 months)

define "in interval 1":
@2020-07-01 in Interval[start of "Measurement Period" - 6 months, start of "Measurement Period" + 6 months)

define "in interval 2":
@2020-07-01T01:00:00.000 in Interval[start of "Measurement Period" - 6 months, start of "Measurement Period" + 6 months)

define "in interval 3":
@2020-07-01T00:59:59.999 in Interval[start of "Measurement Period" - 6 months, start of "Measurement Period" + 6 months)

define "in interval 4":
@2020-07-01 in Interval[@2020-07-01T00:00:00.000, @2021-07-01T00:00:00.000)

define "in interval 5":
@2020-07-01T00:00:00.000 in Interval[@2020-07-01T00:00:00.000, @2021-07-01T00:00:00.000)

define "in interval 6":
@2020-07-01 in day of "the interval"

define After_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameAs_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameOrAfter_HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameOrBefore_SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
