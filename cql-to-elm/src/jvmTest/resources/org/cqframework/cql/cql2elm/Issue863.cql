library Issue863

parameter "Measurement Period" Interval<DateTime> default Interval[@2021-01-01T00:00:00.000, @2022-01-01T00:00:00.000)

define "the interval": //Interval[2020-07-01T00:00:00.000, 2021-07-01T00:00:00.000)
Interval[start of "Measurement Period" - 6 months, start of "Measurement Period" + 6 months)

define "in interval 1": //false
@2020-07-01 in Interval[start of "Measurement Period" - 6 months, start of "Measurement Period" + 6 months)

define "in interval 2": //true
@2020-07-01T01:00:00.000 in Interval[start of "Measurement Period" - 6 months, start of "Measurement Period" + 6 months)

define "in interval 3": //false
@2020-07-01T00:59:59.999 in Interval[start of "Measurement Period" - 6 months, start of "Measurement Period" + 6 months)

define "in interval 4": //true
@2020-07-01 in Interval[@2020-07-01T00:00:00.000, @2021-07-01T00:00:00.000)

define "in interval 5": //true
@2020-07-01T00:00:00.000 in Interval[@2020-07-01T00:00:00.000, @2021-07-01T00:00:00.000)

define "in interval 6": //false
@2020-07-01 in "the interval"
