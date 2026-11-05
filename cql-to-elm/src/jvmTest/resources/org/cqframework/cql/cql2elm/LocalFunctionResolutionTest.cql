define function ToDate(Value DateTime ):
	DateTime(year from Value, month from Value, day from Value, 0, 0, 0, 0, timezoneoffset from Value)

define TestToDate: ToDate(@2018-09-26T23:07:00)