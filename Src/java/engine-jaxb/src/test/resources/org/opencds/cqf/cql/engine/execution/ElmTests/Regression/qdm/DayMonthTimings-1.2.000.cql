library DayMonthTimings version '1.2.000'

using QDM version '5.4'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Months Containing 28 Days":
	/*Identifies the months in the year containing 28 days for determining number of days the medication is prescribed*/
	{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28 }

define "Months Containing 29 Days":
	/*Identifies the months in the year containing 29 days for determining the number of days a medication prescribed*/
	{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29 }

define "Months Containing 30 Days":
	/*Identifies the months in the year containing 30 days for determining the number of days a medication is prescribed*/
	{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 }

define "Months Containing 31 Days":
	/*Identifies the months in the year containing 31 days for determining the number of days a medication is prescribed*/
	{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 }

define "MonthsInYear":
	/*Identifies the number of months in a calendar year to determine the number of days a medication is prescribed*/
	{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }

define function "DaysInMonth"(yr Integer, mo Integer ):
	/*Calculates the number of days of a prescription relevant period*/
	( case
		when mo in { 1, 3, 5, 7, 8, 10, 12 } then "Months Containing 31 Days"
		when mo = 2 then if "IsLeapYear"(yr)then "Months Containing 29 Days" 
			else "Months Containing 28 Days" 
		else "Months Containing 30 Days" 
	end ) D
		return DateTime(yr, mo, D)

define function "IsLeapYear"(yr Integer ):
	/*Determines if year is a leap year*/
	( yr mod 4 = 0
			and yr mod 100 != 0
	)
		or yr mod 400 = 0

define function "DaysInYear"(yr Integer ):
	/*Lists are flattened to a single list*/
	flatten ( MonthsInYear Mo
			return "DaysInMonth"(yr, Mo)
	)

define function "DaysInPeriod"(period Interval<DateTime> ):
	/*Calculates the number of days of a prescription and if the prescription is within the measurement period*/
	case
		when start of period is null
			or 
			end of period is null then null
		when start of period same year as 
		end of period then case
			when month from start of period = month from 
			end of period then ( "DaysInMonth"(year from start of period, month from start of period)) DaysMonth
				where DaysMonth same day or after start of period
					and DaysMonth same day or before 
					end of period 
			else ( "DaysInYear"(year from start of period)) DaysYear
			where DaysYear same day or after start of period
				and DaysYear same day or before 
				end of period 
		end
		when difference in years between start of period and 
		end of period = 1 then ( ( "DaysInYear"(year from start of period)) DaysYear
				where DaysYear same day or after start of period
					and DaysYear same day or before 
					end of period
		)
			union ( ( "DaysInYear"(year from 
					end of period
				)) DaysYear
					where DaysYear same day or after start of period
						and DaysYear same day or before 
						end of period
			) 
		else null 
	end

