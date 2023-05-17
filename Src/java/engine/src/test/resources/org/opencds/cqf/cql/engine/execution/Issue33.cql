library Issue33 version '1'

// Interval
define TestDateTime1: @2017-12-20T11:00:00
define function ToDate(Value DateTime):
  DateTime(year from Value, month from Value, day from Value, 0, 0, 0, 0, timezoneoffset from Value)

define function CalendarDayOf(Value DateTime):
  Interval[Value, ToDate((Value + 1 day)))

define Issue33: CalendarDayOf(TestDateTime1)
