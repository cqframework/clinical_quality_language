library LibraryWithVersion version '1.0.0'

parameter "Measurement Period" Interval<DateTime> default Interval[@2031-01-01, @2032-01-01)

define "Number": 5
define "Period": "Measurement Period"

