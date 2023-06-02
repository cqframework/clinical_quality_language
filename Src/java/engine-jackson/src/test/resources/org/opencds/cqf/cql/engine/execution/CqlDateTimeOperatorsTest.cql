library DateTimeOperator version '1'

//Add
define DateTimeAdd5Years: DateTime(2005, 10, 10) + 5 years
define DateTimeAddInvalidYears: DateTime(2005, 10, 10) + 8000 years
define DateTimeAdd5Months: DateTime(2005, 5, 10) + 5 months
define DateTimeAddMonthsOverflow: DateTime(2005, 5, 10) + 10 months
define DateTimeAddThreeWeeks: DateTime(2018, 5, 2) + 3 weeks
define DateTimeAddYearInWeeks: DateTime(2018, 5, 23) + 52 weeks
define DateTimeAdd5Days: DateTime(2005, 5, 10) + 5 days
define DateTimeAddDaysOverflow: DateTime(2016, 6, 10) + 21 days
define DateTimeAdd5Hours: DateTime(2005, 5, 10, 5) + 5 hours
define DateTimeAdd5HoursWithLeftMinPrecisionSecond: DateTime(2005, 5, 10, 5, 20, 30) + 5 hours
define DateTimeAdd5HoursWithLeftMinPrecisionDay: DateTime(2005, 5, 10) + 5 hours
define DateTimeAdd5HoursWithLeftMinPrecisionDayOverflow: DateTime(2005, 5, 10) + 25 hours
define DateAdd2YearsAsMonths: Date(2014) + 24 months
define DateAdd2YearsAsMonthsRem1: Date(2014) + 25 months
define DateAdd33Days: Date(2014,6) + 33 days
define DateAdd1Year: Date(2014,6) + 1 year
define DateTimeAddHoursOverflow: DateTime(2016, 6, 10, 5) + 19 hours
define DateTimeAdd5Minutes: DateTime(2005, 5, 10, 5, 5) + 5 minutes
define DateTimeAddMinutesOverflow: DateTime(2016, 6, 10, 5, 5) + 55 minutes
define DateTimeAdd5Seconds: DateTime(2005, 5, 10, 5, 5, 5) + 5 seconds
define DateTimeAddSecondsOverflow: DateTime(2016, 6, 10, 5, 5, 5) + 55 seconds
define DateTimeAdd5Milliseconds: DateTime(2005, 5, 10, 5, 5, 5, 5) + 5 milliseconds
define DateTimeAddMillisecondsOverflow: DateTime(2016, 6, 10, 5, 5, 5, 5) + 995 milliseconds
define DateTimeAddLeapYear: DateTime(2012, 2, 29) + 1 year
define DateTimeAdd2YearsByMonths: DateTime(2014) + 24 months
define DateTimeAdd2YearsByDays: DateTime(2014) + 730 days
define DateTimeAdd2YearsByDaysRem5Days: DateTime(2014) + 735 days
define TimeAdd5Hours: @T15:59:59.999 + 5 hours
define TimeAdd1Minute: @T15:59:59.999 + 1 minute
define TimeAdd1Second: @T15:59:59.999 + 1 seconds
define TimeAdd1Millisecond: @T15:59:59.999 + 1 milliseconds
define TimeAdd5Hours1Minute: @T15:59:59.999 + 5 hours + 1 minutes
define TimeAdd5hoursByMinute: @T15:59:59.999 + 300 minutes

//After
define DateTimeAfterYearTrue: DateTime(2005, 10, 10) after year of DateTime(2004, 10, 10)
define DateTimeAfterYearFalse: DateTime(2004, 11, 10) after year of DateTime(2004, 10, 10)
define DateTimeAfterMonthTrue: DateTime(2004, 12, 10) after month of DateTime(2004, 11, 10)
define DateTimeAfterMonthFalse: DateTime(2004, 9, 10) after month of DateTime(2004, 10, 10)
define DateTimeAfterDayTrue: DateTime(2004, 12, 11) after day of DateTime(2004, 10, 10)
define DateTimeAfterDayTrue2: DateTime(2004, 12, 09) after day of DateTime(2003, 10, 10)
define DateTimeAfterDayFalse: DateTime(2004, 10, 9) after day of DateTime(2004, 10, 10)
define DateTimeAfterHourTrue: DateTime(2004, 10, 10, 10) after hour of DateTime(2004, 10, 10, 5)
define DateTimeAfterHourFalse: DateTime(2004, 10, 10, 20) after hour of DateTime(2004, 10, 10, 21)
define DateTimeAfterMinuteTrue: DateTime(2004, 10, 10, 20, 30) after minute of DateTime(2004, 10, 10, 20, 29)
define DateTimeAfterMinuteFalse: DateTime(2004, 10, 10, 20, 30) after minute of DateTime(2004, 10, 10, 20, 31)
define DateTimeAfterSecondTrue: DateTime(2004, 10, 10, 20, 30, 15) after second of DateTime(2004, 10, 10, 20, 30, 14)
define DateTimeAfterSecondFalse: DateTime(2004, 10, 10, 20, 30, 15) after second of DateTime(2004, 10, 10, 20, 30, 16)
define DateTimeAfterMillisecondTrue: DateTime(2004, 10, 10, 20, 30, 15, 512) after millisecond of DateTime(2004, 10, 10, 20, 30, 15, 510)
define DateTimeAfterMillisecondFalse: DateTime(2004, 10, 10, 20, 30, 15, 512) after millisecond of DateTime(2004, 10, 10, 20, 30, 15, 513)
define DateTimeAfterUncertain: DateTime(2005, 10, 10) after day of DateTime(2005, 9)
define AfterTimezoneTrue: @2012-03-10T10:20:00.999+07:00 after hour of @2012-03-10T08:20:00.999+06:00
define AfterTimezoneFalse: @2012-03-10T10:20:00.999+07:00 after hour of @2012-03-10T10:20:00.999+06:00
define TimeAfterHourTrue: @T15:59:59.999 after hour of @T14:59:59.999
define TimeAfterHourFalse: @T15:59:59.999 after hour of @T16:59:59.999
define TimeAfterMinuteTrue: @T15:59:59.999 after minute of @T15:58:59.999
define TimeAfterMinuteFalse: @T15:58:59.999 after minute of @T15:59:59.999
define TimeAfterSecondTrue: @T15:59:59.999 after second of @T15:59:58.999
define TimeAfterSecondFalse: @T15:59:58.999 after second of @T15:59:59.999
define TimeAfterMillisecondTrue: @T15:59:59.999 after millisecond of @T15:59:59.998
define TimeAfterMillisecondFalse: @T15:59:59.998 after millisecond of @T15:59:59.999
define TimeAfterTimeCstor: Time(12, 30) after hour of Time(11, 55)
// TODO: do uncertainty tests for Time once the Time(x,x,x,x,x) format has been implemented

//Before
define DateTimeBeforeYearTrue: DateTime(2003) before year of DateTime(2004, 10, 10)
define DateTimeBeforeYearFalse: DateTime(2004, 11, 10) before year of DateTime(2003, 10, 10)
define DateTimeBeforeMonthTrue: DateTime(2004, 10, 10) before month of DateTime(2004, 11, 10)
define DateTimeBeforeMonthFalse: DateTime(2004, 11, 10) before month of DateTime(2004, 10, 10)
define DateTimeBeforeDayTrue: DateTime(2004, 10, 1) before day of DateTime(2004, 10, 10)
define DateTimeBeforeDayTrue2: DateTime(2003, 10, 11) before day of DateTime(2004, 10, 10)
define DateTimeBeforeDayFalse: DateTime(2004, 10, 11) before day of DateTime(2004, 10, 10)
define DateTimeBeforeHourTrue: DateTime(2004, 10, 10, 1) before hour of DateTime(2004, 10, 10, 5)
define DateTimeBeforeHourFalse: DateTime(2004, 10, 10, 23) before hour of DateTime(2004, 10, 10, 21)
define DateTimeBeforeMinuteTrue: DateTime(2004, 10, 10, 20, 28) before minute of DateTime(2004, 10, 10, 20, 29)
define DateTimeBeforeMinuteFalse: DateTime(2004, 10, 10, 20, 35) before minute of DateTime(2004, 10, 10, 20, 31)
define DateTimeBeforeSecondTrue: DateTime(2004, 10, 10, 20, 30, 12) before second of DateTime(2004, 10, 10, 20, 30, 14)
define DateTimeBeforeSecondFalse: DateTime(2004, 10, 10, 20, 30, 55) before second of DateTime(2004, 10, 10, 20, 30, 16)
define DateTimeBeforeMillisecondTrue: DateTime(2004, 10, 10, 20, 30, 15, 508) before millisecond of DateTime(2004, 10, 10, 20, 30, 15, 510)
define DateTimeBeforeMillisecondFalse: DateTime(2004, 10, 10, 20, 30, 15, 599) before millisecond of DateTime(2004, 10, 10, 20, 30, 15, 513)
define BeforeTimezoneTrue: @2012-03-10T10:20:00.999+07:00 before hour of @2012-03-10T10:20:00.999+06:00
define BeforeTimezoneFalse: @2012-03-10T10:20:00.999+07:00 before hour of @2012-03-10T09:20:00.999+06:00
define TimeBeforeHourTrue: @T13:59:59.999 before hour of @T14:59:59.999
define TimeBeforeHourFalse: @T16:59:59.999 before hour of @T15:59:59.999
define TimeBeforeMinuteTrue: @T15:57:59.999 before minute of @T15:58:59.999
define TimeBeforeMinuteFalse: @T15:59:59.999 before minute of @T15:59:59.999
define TimeBeforeSecondTrue: @T15:59:57.999 before second of @T15:59:58.999
define TimeBeforeSecondFalse: @T15:59:56.999 before second of @T15:59:55.999
define TimeBeforeMillisecondTrue: @T15:59:59.997 before millisecond of @T15:59:59.998
define TimeBeforeMillisecondFalse: @T15:59:59.998 before millisecond of @T15:59:59.997
// TODO: do uncertainty tests for Time once the Time(x,x,x,x,x) format has been implemented

//DateTime
define DateTimeYear: DateTime(2003)
define DateTimeMonth: DateTime(2003, 10)
define DateTimeDay: DateTime(2003, 10, 29)
define DateTimeHour: DateTime(2003, 10, 29, 20)
define DateTimeMinute: DateTime(2003, 10, 29, 20, 50)
define DateTimeSecond: DateTime(2003, 10, 29, 20, 50, 33)
define DateTimeMillisecond: DateTime(2003, 10, 29, 20, 50, 33, 955)

//DateTimeComponentFrom
define DateTimeComponentFromYear: year from DateTime(2003, 10, 29, 20, 50, 33, 955)
define DateTimeComponentFromMonth: month from DateTime(2003, 10, 29, 20, 50, 33, 955)
define DateTimeComponentFromMonthMinBoundary: month from DateTime(2003, 01, 29, 20, 50, 33, 955)
define DateTimeComponentFromDay: day from DateTime(2003, 10, 29, 20, 50, 33, 955)
define DateTimeComponentFromHour: hour from DateTime(2003, 10, 29, 20, 50, 33, 955)
define DateTimeComponentFromMinute: minute from DateTime(2003, 10, 29, 20, 50, 33, 955)
define DateTimeComponentFromSecond: second from DateTime(2003, 10, 29, 20, 50, 33, 955)
define DateTimeComponentFromMillisecond: millisecond from DateTime(2003, 10, 29, 20, 50, 33, 955)
define DateTimeComponentFromTimezone: timezoneoffset from DateTime(2003, 10, 29, 20, 50, 33, 955, 1)
define DateTimeComponentFromDate: date from DateTime(2003, 10, 29, 20, 50, 33, 955, 1)
define TimeComponentFromHour: hour from @T23:20:15.555
define TimeComponentFromMinute: minute from @T23:20:15.555
define TimeComponentFromSecond: second from @T23:20:15.555
define TimeComponentFromMilli: millisecond from @T23:20:15.555

//Difference
define DateTimeDifferenceYear: difference in years between DateTime(2000) and DateTime(2005, 12)
define DateTimeDifferenceMonth: difference in months between DateTime(2000, 2) and DateTime(2000, 10)
define DateTimeDifferenceDay: difference in days between DateTime(2000, 10, 15, 10, 30) and DateTime(2000, 10, 25, 10, 0)
define DateTimeDifferenceHour: difference in hours between DateTime(2000, 4, 1, 12) and DateTime(2000, 4, 1, 20)
define DateTimeDifferenceMinute: difference in minutes between DateTime(2005, 12, 10, 5, 16) and DateTime(2005, 12, 10, 5, 25)
define DateTimeDifferenceSecond: difference in seconds between DateTime(2000, 10, 10, 10, 5, 45) and DateTime(2000, 10, 10, 10, 5, 50)
define DateTimeDifferenceMillisecond: difference in milliseconds between DateTime(2000, 10, 10, 10, 5, 45, 500, -6.0) and DateTime(2000, 10, 10, 10, 5, 45, 900, -7.0)
define DateTimeDifferenceWeeks: difference in weeks between DateTime(2000, 10, 15) and DateTime(2000, 10, 28)
define DateTimeDifferenceWeeks2: difference in weeks between DateTime(2000, 10, 15) and DateTime(2000, 10, 29)
define DateTimeDifferenceWeeks3: difference in weeks between @2012-03-10T22:05:09 and @2012-03-24T07:19:33
define DateTimeDifferenceNegative: difference in years between DateTime(2016) and DateTime(1998)
define DateTimeDifferenceUncertain: difference in months between DateTime(2005) and DateTime(2006, 7) > 5
define TimeDifferenceHour: difference in hours between @T20 and @T23:25:15.555
define TimeDifferenceMinute: difference in minutes between @T20:20:15.555 and @T20:25:15.555
define TimeDifferenceSecond: difference in seconds between @T20:20:15.555 and @T20:20:20.555
define TimeDifferenceMillis: difference in milliseconds between @T20:20:15.555 and @T20:20:15.550

// From Github issue #29
define DateTimeA: @2017-03-12T01:00:00-07:00 // March 12, 2017, 1:00 AM MST
define DateTimeAA: DateTime(2017, 3, 12, 1, 0, 0, 0, -7.0)
define DateTimeB: @2017-03-12T03:00:00-06:00 // 3:00 AM MDT
define DateTimeBB: DateTime(2017, 3, 12, 3, 0, 0, 0, -6.0)
define DateTimeC: @2017-11-05T01:30:00-06:00 // November 5, 2017, 1:30 AM MDT
define DateTimeCC: DateTime(2017, 11, 5, 1, 30, 0, 0, -6.0)
define DateTimeD: @2017-11-05T01:15:00-07:00 // November 5, 2017, 1:15 AM MST
define DateTimeDD: DateTime(2017, 11, 5, 1, 15, 0, 0, -7.0)
define DateTimeE: @2017-03-12T00:00:00-07:00 // March 12, 2017
define DateTimeEE: DateTime(2017, 3, 12, 0, 0, 0, 0, -7.0)
define DateTimeF: @2017-03-13T00:00:00-06:00 // March 13, 2017
define DateTimeFF: DateTime(2017, 3, 13, 0, 0, 0, 0, -6.0)

define DifferenceInHoursA: difference in hours between DateTimeA and DateTimeB // Should be 1
define DifferenceInMinutesA: difference in minutes between DateTimeC and DateTimeD // Should be 45
define DifferenceInDaysA: difference in days between DateTimeE and DateTimeF // Should be 1
define DifferenceInHoursAA: difference in hours between DateTimeAA and DateTimeBB // Should be 1
define DifferenceInMinutesAA: difference in minutes between DateTimeCC and DateTimeDD // Should be 45
define DifferenceInDaysAA: difference in days between DateTimeEE and DateTimeFF // Should be 1
// TODO: Time uncertainty tests

//Duration
define DateTimeDurationBetweenYear: years between DateTime(2005) and DateTime(2010)
define DateTimeDurationBetweenYearOffset: years between DateTime(2005, 5) and DateTime(2010, 4)
define DateTimeDurationBetweenMonth: months between DateTime(2014, 1, 31) and DateTime(2014, 2, 1)
define DateTimeDurationBetweenDaysDiffYears: days between DateTime(2010, 10, 12, 12, 5) and DateTime(2008, 8, 15, 8, 8)
// Uncertainty tests
define DateTimeDurationBetweenUncertainInterval: days between DateTime(2014, 1, 15) and DateTime(2014, 2)
define DateTimeDurationBetweenUncertainInterval2: months between DateTime(2005) and DateTime(2006, 5)
define DateTimeDurationBetweenUncertainAdd: DateTimeDurationBetweenUncertainInterval + DateTimeDurationBetweenUncertainInterval
define DateTimeDurationBetweenUncertainSubtract: DateTimeDurationBetweenUncertainInterval - DateTimeDurationBetweenUncertainInterval2
define DateTimeDurationBetweenUncertainMultiply: DateTimeDurationBetweenUncertainInterval * DateTimeDurationBetweenUncertainInterval
define DateTimeDurationBetweenUncertainDiv: DateTimeDurationBetweenUncertainInterval div DateTimeDurationBetweenUncertainInterval2
define DateTimeDurationBetweenMonthUncertain: months between DateTime(2005) and DateTime(2006, 7) > 5
define DateTimeDurationBetweenMonthUncertain2: months between DateTime(2005) and DateTime(2006, 2) > 5
define DateTimeDurationBetweenMonthUncertain3: months between DateTime(2005) and DateTime(2006, 7) > 25
define DateTimeDurationBetweenMonthUncertain4: months between DateTime(2005) and DateTime(2006, 7) < 24
define DateTimeDurationBetweenMonthUncertain5: months between DateTime(2005) and DateTime(2006, 7) = 24
define DateTimeDurationBetweenMonthUncertain6: months between DateTime(2005) and DateTime(2006, 7) >= 5
define DateTimeDurationBetweenMonthUncertain7: months between DateTime(2005) and DateTime(2006, 7) <= 24

define DateTime1: @2012-03-10T10:20:00
define DateTime2: @2013-03-10T09:20:00
define DurationInYears: years between (date from DateTime1) and (date from DateTime2)

define DurationInWeeks: weeks between @2012-03-10T22:05:09 and @2012-03-20T07:19:33
define DurationInWeeks2: weeks between @2012-03-10T22:05:09 and @2012-03-24T07:19:33
define DurationInWeeks3: weeks between @2012-03-10T06:05:09 and @2012-03-24T07:19:33

define TimeDurationBetweenHour: hours between @T20:26:15.555 and @T23:25:15.555
define TimeDurationBetweenHourDiffPrecision: hours between @T06 and @T07:00:00
define TimeDurationBetweenMinute: minutes between @T23:20:16.555 and @T23:25:15.555
define TimeDurationBetweenSecond: seconds between @T23:25:10.556 and @T23:25:15.555
define TimeDurationBetweenMillis: milliseconds between @T23:25:25.555 and @T23:25:25.560
// TODO: Time uncertainty tests

define DurationInHoursA: hours between DateTimeA and DateTimeB // Should be 1
define DurationInMinutesA: minutes between DateTimeC and DateTimeD // Should be 45
define DurationInDaysA: days between DateTimeE and DateTimeF // Should be 1
define DurationInHoursAA: hours between DateTimeAA and DateTimeBB // Should be 1
define DurationInMinutesAA: minutes between DateTimeCC and DateTimeDD // Should be 45
define DurationInDaysAA: days between DateTimeEE and DateTimeFF // Should be 1
//Now
define DateTimeNow: Now() = Now()
define Issue34A: Now()

//SameAs
define DateTimeSameAsYearTrue: DateTime(2014) same year as DateTime(2014)
define DateTimeSameAsYearFalse: DateTime(2013) same year as DateTime(2014)
define DateTimeSameAsMonthTrue: DateTime(2014, 12) same month as DateTime(2014, 12)
define DateTimeSameAsMonthFalse: DateTime(2014, 12) same month as DateTime(2014, 10)
define DateTimeSameAsDayTrue: DateTime(2014, 12, 10) same day as DateTime(2014, 12, 10)
define DateTimeSameAsDayFalse: DateTime(2014, 10, 10) same day as DateTime(2014, 10, 11)
define DateTimeSameAsHourTrue: DateTime(2014, 12, 10, 20) same hour as DateTime(2014, 12, 10, 20)
define DateTimeSameAsHourFalse: DateTime(2014, 10, 10, 20) same hour as DateTime(2014, 10, 10, 21)
define DateTimeSameAsMinuteTrue: DateTime(2014, 12, 10, 20, 55) same minute as DateTime(2014, 12, 10, 20, 55)
define DateTimeSameAsMinuteFalse: DateTime(2014, 10, 10, 20, 55) same minute as DateTime(2014, 10, 10, 21, 56)
define DateTimeSameAsSecondTrue: DateTime(2014, 12, 10, 20, 55, 45) same second as DateTime(2014, 12, 10, 20, 55, 45)
define DateTimeSameAsSecondFalse: DateTime(2014, 10, 10, 20, 55, 45) same second as DateTime(2014, 10, 10, 21, 55, 44)
define DateTimeSameAsMillisecondTrue: DateTime(2014, 12, 10, 20, 55, 45, 500) same millisecond as DateTime(2014, 12, 10, 20, 55, 45, 500)
define DateTimeSameAsMillisecondFalse: DateTime(2014, 10, 10, 20, 55, 45, 500) same millisecond as DateTime(2014, 10, 10, 21, 55, 45, 501)
define DateTimeSameAsNull: DateTime(2014, 10) same day as DateTime(2014, 10, 12)
define SameAsTimezoneTrue: @2012-03-10T10:20:00.999+07:00 same hour as @2012-03-10T09:20:00.999+06:00
define SameAsTimezoneFalse: @2012-03-10T10:20:00.999+07:00 same hour as @2012-03-10T10:20:00.999+06:00
define TimeSameAsHourTrue: @T23:25:25.555 same hour as @T23:55:25.900
define TimeSameAsHourFalse: @T22:25:25.555 same hour as @T23:25:25.555
define TimeSameAsMinuteTrue: @T23:55:22.555 same minute as @T23:55:25.900
define TimeSameAsMinuteFalse: @T23:26:25.555 same minute as @T23:25:25.555
define TimeSameAsSecondTrue: @T23:55:25.555 same second as @T23:55:25.900
define TimeSameAsSecondFalse: @T23:25:35.555 same second as @T23:25:25.555
define TimeSameAsMillisTrue: @T23:55:25.555 same millisecond as @T23:55:25.555
define TimeSameAsMillisFalse: @T23:25:25.555 same millisecond as @T23:25:25.554

//SameOrAfter
define DateTimeSameOrAfterYearTrue1: DateTime(2014) same year or after DateTime(2014)
define DateTimeSameOrAfterYearTrue2: DateTime(2016) same year or after DateTime(2014)
define DateTimeSameOrAfterYearFalse: DateTime(2013) same year or after DateTime(2014)
define DateTimeSameOrAfterMonthTrue1: DateTime(2014, 12) same month or after DateTime(2014, 12)
define DateTimeSameOrAfterMonthTrue2: DateTime(2014, 10) same month or after DateTime(2014, 9)
define DateTimeSameOrAfterMonthFalse: DateTime(2014, 10) same month or after DateTime(2014, 11)
define DateTimeSameOrAfterDayTrue1: DateTime(2014, 12, 20) same day or after DateTime(2014, 12, 20)
define DateTimeSameOrAfterDayTrue2: DateTime(2014, 10, 25) same day or after DateTime(2014, 10, 20)
define DateTimeSameOrAfterDayFalse: DateTime(2014, 10, 20) same day or after DateTime(2014, 10, 25)
define DateTimeSameOrAfterHourTrue1: DateTime(2014, 12, 20, 12) same hour or after DateTime(2014, 12, 20, 12)
define DateTimeSameOrAfterHourTrue2: DateTime(2014, 10, 25, 12) same hour or after DateTime(2014, 10, 25, 10)
define DateTimeSameOrAfterHourFalse: DateTime(2014, 10, 25, 12) same hour or after DateTime(2014, 10, 25, 15)
define DateTimeSameOrAfterMinuteTrue1: DateTime(2014, 12, 20, 12, 30) same minute or after DateTime(2014, 12, 20, 12, 30)
define DateTimeSameOrAfterMinuteTrue2: DateTime(2014, 10, 25, 10, 30) same minute or after DateTime(2014, 10, 25, 10, 25)
define DateTimeSameOrAfterMinuteFalse: DateTime(2014, 10, 25, 15, 30) same minute or after DateTime(2014, 10, 25, 15, 45)
define DateTimeSameOrAfterSecondTrue1: DateTime(2014, 12, 20, 12, 30, 15) same second or after DateTime(2014, 12, 20, 12, 30, 15)
define DateTimeSameOrAfterSecondTrue2: DateTime(2014, 10, 25, 10, 25, 25) same second or after DateTime(2014, 10, 25, 10, 25, 20)
define DateTimeSameOrAfterSecondFalse: DateTime(2014, 10, 25, 15, 45, 20) same second or after DateTime(2014, 10, 25, 15, 45, 21)
define DateTimeSameOrAfterMillisecondTrue1: DateTime(2014, 12, 20, 12, 30, 15, 250) same millisecond or after DateTime(2014, 12, 20, 12, 30, 15, 250)
define DateTimeSameOrAfterMillisecondTrue2: DateTime(2014, 10, 25, 10, 25, 20, 500) same millisecond or after DateTime(2014, 10, 25, 10, 25, 20, 499)
define DateTimeSameOrAfterMillisecondFalse: DateTime(2014, 10, 25, 15, 45, 20, 500) same millisecond or after DateTime(2014, 10, 25, 15, 45, 20, 501)
define DateTimeSameOrAfterNull1: DateTime(2014, 12, 20) same day or after DateTime(2014, 12)
define SameOrAfterTimezoneTrue: @2012-03-10T10:20:00.999+07:00 same hour or after @2012-03-10T09:20:00.999+06:00
define SameOrAfterTimezoneFalse: @2012-03-10T10:20:00.999+07:00 same hour or after @2012-03-10T10:20:00.999+06:00
define TimeSameOrAfterHourTrue1: @T23:25:25.555 same hour or after @T23:55:25.900
define TimeSameOrAfterHourTrue2: @T23:25:25.555 same hour or after @T22:55:25.900
define TimeSameOrAfterHourFalse: @T22:25:25.555 same hour or after @T23:55:25.900
define TimeSameOrAfterMinuteTrue1: @T23:25:25.555 same minute or after @T23:25:25.900
define TimeSameOrAfterMinuteTrue2: @T23:25:25.555 same minute or after @T22:15:25.900
define TimeSameOrAfterMinuteFalse: @T23:25:25.555 same minute or after @T23:55:25.900
define TimeSameOrAfterSecondTrue1: @T23:25:25.555 same second or after @T23:25:25.900
define TimeSameOrAfterSecondTrue2: @T23:25:35.555 same second or after @T22:25:25.900
define TimeSameOrAfterSecondFalse: @T23:55:25.555 same second or after @T23:55:35.900
define TimeSameOrAfterMillisTrue1: @T23:25:25.555 same millisecond or after @T23:25:25.555
define TimeSameOrAfterMillisTrue2: @T23:25:25.555 same millisecond or after @T22:25:25.550
define TimeSameOrAfterMillisFalse: @T23:55:25.555 same millisecond or after @T23:55:25.900
define OnOrAfterTrue: @2017-12-20T11:00:00.000 on or after @2017-12-20T11:00:00.000
define Issue32DateTime: @2017-12-21T02:00:00.0 same or after @2017-12-20T11:00:00.0

//SameOrBefore
define DateTimeSameOrBeforeYearTrue1: DateTime(2014) same year or before DateTime(2014)
define DateTimeSameOrBeforeYearTrue2: DateTime(2013) same year or before DateTime(2014)
define DateTimeSameOrBeforeYearFalse: DateTime(2015) same year or before DateTime(2014)
define DateTimeSameOrBeforeMonthTrue1: DateTime(2014, 12) same month or before DateTime(2014, 12)
define DateTimeSameOrBeforeMonthTrue2: DateTime(2014, 8) same month or before DateTime(2014, 9)
define DateTimeSameOrBeforeMonthFalse: DateTime(2014, 12) same month or before DateTime(2014, 11)
define DateTimeSameOrBeforeDayTrue1: DateTime(2014, 12, 20) same day or before DateTime(2014, 12, 20)
define DateTimeSameOrBeforeDayTrue2: DateTime(2014, 10, 15) same day or before DateTime(2014, 10, 20)
define DateTimeSameOrBeforeDayFalse: DateTime(2014, 10, 30) same day or before DateTime(2014, 10, 25)
define DateTimeSameOrBeforeHourTrue1: DateTime(2014, 12, 20, 12) same hour or before DateTime(2014, 12, 20, 12)
define DateTimeSameOrBeforeHourTrue2: DateTime(2014, 10, 25, 5) same hour or before DateTime(2014, 10, 25, 10)
define DateTimeSameOrBeforeHourFalse: DateTime(2014, 10, 25, 20) same hour or before DateTime(2014, 10, 25, 15)
define DateTimeSameOrBeforeMinuteTrue1: DateTime(2014, 12, 20, 12, 30) same minute or before DateTime(2014, 12, 20, 12, 30)
define DateTimeSameOrBeforeMinuteTrue2: DateTime(2014, 10, 25, 10, 20) same minute or before DateTime(2014, 10, 25, 10, 25)
define DateTimeSameOrBeforeMinuteFalse: DateTime(2014, 10, 25, 15, 55) same minute or before DateTime(2014, 10, 25, 15, 45)
define DateTimeSameOrBeforeSecondTrue1: DateTime(2014, 12, 20, 12, 30, 15) same second or before DateTime(2014, 12, 20, 12, 30, 15)
define DateTimeSameOrBeforeSecondTrue2: DateTime(2014, 10, 25, 10, 25, 15) same second or before DateTime(2014, 10, 25, 10, 25, 20)
define DateTimeSameOrBeforeSecondFalse: DateTime(2014, 10, 25, 15, 45, 25) same second or before DateTime(2014, 10, 25, 15, 45, 21)
define DateTimeSameOrBeforeMillisecondTrue1: DateTime(2014, 12, 20, 12, 30, 15, 250) same millisecond or before DateTime(2014, 12, 20, 12, 30, 15, 250)
define DateTimeSameOrBeforeMillisecondTrue2: DateTime(2014, 10, 25, 10, 25, 20, 450) same millisecond or before DateTime(2014, 10, 25, 10, 25, 20, 499)
define DateTimeSameOrBeforeMillisecondFalse: DateTime(2014, 10, 25, 15, 45, 20, 505) same millisecond or before DateTime(2014, 10, 25, 15, 45, 20, 501)
define DateTimeSameOrBeforeNull1: DateTime(2014, 12, 20) same minute or before DateTime(2014, 12, 20, 15)
define SameOrBeforeTimezoneTrue: @2012-03-10T09:20:00.999+07:00 same hour or before @2012-03-10T10:20:00.999+06:00
define SameOrBeforeTimezoneFalse: @2012-03-10T10:20:00.999+06:00 same hour or before @2012-03-10T10:20:00.999+07:00
define TimeSameOrBeforeHourTrue1: @T23:25:25.555 same hour or before @T23:55:25.900
define TimeSameOrBeforeHourFalse0: @T21:25:25.555 same hour or before @T22:55:25.900
define TimeSameOrBeforeHourFalse: @T22:25:25.555 same hour or before @T21:55:25.900
define TimeSameOrBeforeMinuteTrue1: @T23:25:25.555 same minute or before @T23:25:25.900
define TimeSameOrBeforeMinuteFalse0: @T23:10:25.555 same minute or before @T22:15:25.900
define TimeSameOrBeforeMinuteFalse: @T23:56:25.555 same minute or before @T23:55:25.900
define TimeSameOrBeforeSecondTrue1: @T23:25:25.555 same second or before @T23:25:25.900
define TimeSameOrBeforeSecondFalse0: @T23:25:35.555 same second or before @T22:25:45.900
define TimeSameOrBeforeSecondFalse: @T23:55:45.555 same second or before @T23:55:35.900
define TimeSameOrBeforeMillisTrue1: @T23:25:25.555 same millisecond or before @T23:25:25.555
define TimeSameOrBeforeMillisTrue2: @T23:25:25.200 same millisecond or before @T22:25:25.550
define TimeSameOrBeforeMillisFalse: @T23:55:25.966 same millisecond or before @T23:55:25.900

//Subtract
define DateTimeSubtract5Years: DateTime(2005, 10, 10) - 5 years
define DateTimeSubtractInvalidYears: DateTime(2005, 10, 10) - 2005 years
define DateTimeSubtract5Months: DateTime(2005, 6, 10) - 5 months
define DateTimeSubtractMonthsUnderflow: DateTime(2005, 5, 10) - 6 months
define DateTimeSubtractThreeWeeks: DateTime(2018, 5, 23) - 3 weeks
define DateTimeSubtractYearInWeeks: DateTime(2018, 5, 23) - 52 weeks
define DateTimeSubtract5Days: DateTime(2005, 5, 10) - 5 days
define DateTimeSubtractDaysUnderflow: DateTime(2016, 6, 10) - 11 days
define DateTimeSubtract5Hours: DateTime(2005, 5, 10, 10) - 5 hours
define DateTimeSubtractHoursUnderflow: DateTime(2016, 6, 10, 5) - 6 hours
define DateTimeSubtract5Minutes: DateTime(2005, 5, 10, 5, 10) - 5 minutes
define DateTimeSubtractMinutesUnderflow: DateTime(2016, 6, 10, 5, 5) - 6 minutes
define DateTimeSubtract5Seconds: DateTime(2005, 5, 10, 5, 5, 10) - 5 seconds
define DateTimeSubtract1YearInSeconds: DateTime(2016,5) - 31535999 seconds
define DateTimeSubtract15HourPrecisionSecond: DateTime(2016, 10, 1, 10, 20, 30) - 15 hours
define DateTimeSubtractSecondsUnderflow: DateTime(2016, 6, 10, 5, 5, 5) - 6 seconds
define DateTimeSubtract5Milliseconds: DateTime(2005, 5, 10, 5, 5, 5, 10) - 5 milliseconds
define DateTimeSubtractMillisecondsUnderflow: DateTime(2016, 6, 10, 5, 5, 5, 5) - 6 milliseconds
define DateTimeSubtract2YearsAsMonths: DateTime(2014) - 24 months
define DateTimeSubtract2YearsAsMonthsRem1: DateTime(2014) - 25 months
define DateSubtract2YearsAsMonths: Date(2014) - 24 months
define DateSubtract2YearsAsMonthsRem1: Date(2014) - 25 months
define DateSubtract33Days: Date(2014,6) - 33 days
define DateSubtract1Year: Date(2014,6) - 1 year
define TimeSubtract5Hours: @T15:59:59.999 - 5 hours
define TimeSubtract1Minute: @T15:59:59.999 - 1 minutes
define TimeSubtract1Second: @T15:59:59.999 - 1 seconds
define TimeSubtract1Millisecond: @T15:59:59.0 - 1 milliseconds
define TimeSubtract5Hours1Minute: @T15:59:59.999 - 5 hours - 1 minutes
define TimeSubtract5hoursByMinute: @T15:59:59.999 - 300 minutes

//Time
define TimeTest2: @T23:59:59.999

//TimeOfDay
define TimeOfDayTest: TimeOfDay()

//Today
define DateTimeSameOrBeforeTodayTrue1: Today() same day or before Today()
define DateTimeSameOrBeforeTodayTrue2: Today() same day or before Today() + 1 days
define DateTimeSameOrBeforeTodayFalse: Today() + 1 years same day or before Today()
define DateTimeAddTodayTrue: Today() + 1 days > Today()
define Issue34B: Today()
