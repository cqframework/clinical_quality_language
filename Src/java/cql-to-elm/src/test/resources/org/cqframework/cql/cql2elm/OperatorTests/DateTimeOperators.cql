// DateTime Operators
define DateTimeAdd: DateTime(2014, 1, 1) + 1 day
define DateTimeAfter: DateTime(2014, 12, 31) after DateTime(2014, 1, 1)
define DateTimeBefore: DateTime(2014, 1, 1) before DateTime(2014, 12, 31)
define DateTimeYear: DateTime(2014)
define DateTimeMonth: DateTime(2014, 1)
define DateTimeDay: DateTime(2014, 1, 1)
define DateTimeHour: DateTime(2014, 1, 1, 0)
define DateTimeMinute: DateTime(2014, 1, 1, 0, 0)
define DateTimeSecond: DateTime(2014, 1, 1, 0, 0, 0)
define DateTimeMillisecond: DateTime(2014, 1, 1, 0, 0, 0, 0)
define DateTimeMillisecondOffset: DateTime(2014, 1, 1, 0, 0, 0, 0, 5.5)
define DateExtractor: date from DateTime(2014, 1, 1, 14, 30, 0, 0)
define TimeExtractor: time from DateTime(2014, 1, 1, 14, 30, 0, 0)
define DateTimeTimezoneExtractor: timezone from DateTime(2014, 1, 1, 14, 30, 0, 0)
define YearFromDateTime: year from DateTime(2014, 1, 1, 0, 0, 0, 0)
define MonthFromDateTime: month from DateTime(2014, 1, 1, 0, 0, 0, 0)
define DayFromDateTime: day from DateTime(2014, 1, 1, 0, 0, 0, 0)
define HourFromDateTime: hour from DateTime(2014, 1, 1, 0, 0, 0, 0)
define MinuteFromDateTime: minute from DateTime(2014, 1, 1, 0, 0, 0, 0)
define SecondFromDateTime: second from DateTime(2014, 1, 1, 0, 0, 0, 0)
define MillisecondFromDateTime: millisecond from DateTime(2014, 1, 1, 0, 0, 0, 0)
define DateTimeDifferenceBetweenExpression: difference in days between DateTime(2014, 1, 1) and DateTime(2014, 12, 31)
define DateTimeDurationBetweenExpression: days between DateTime(2014, 1, 1) and DateTime(2014, 12, 31)
define DateTimeDurationInBetweenExpression: duration in days between DateTime(2014, 1, 1) and DateTime(2014, 12, 31)
define DateTimeDifferenceExpression: difference in days of Interval[DateTime(2014, 1, 1), DateTime(2014, 12, 31)]
define DateTimeDurationExpression: duration in days of Interval[DateTime(2014, 1, 1), DateTime(2014, 12, 31)]
define DateTimeSameAsExpression: DateTime(2014, 1, 1) same day as DateTime(2014, 1, 1)
define DateTimeSameOrAfterExpression: DateTime(2014, 1, 1) same day or after DateTime(2014, 1, 1)
define DateTimeSameOrBeforeExpression: DateTime(2014, 1, 1) same day or before DateTime(2014, 1, 1)
define DateTimeSubtract: DateTime(2014, 1, 1) - 1 day
define NowExpression: Now()

// DateOperators
define DateAdd: Date(2014, 1, 1) + 1 day
define DateAfter: Date(2014, 12, 31) after Date(2014, 1, 1)
define DateBefore: Date(2014, 1, 1) before Date(2014, 12, 31)
define DateYear: Date(2014)
define DateMonth: Date(2014, 1)
define DateDay: Date(2014, 1, 1)
define DateTimezoneExtractor: timezone from Date(2014, 1, 1)
define YearFromDate: year from Date(2014, 1, 1)
define MonthFromDate: month from Date(2014, 1, 1)
define DayFromDate: day from Date(2014, 1, 1)
define DateDifferenceBetweenExpression: difference in days between Date(2014, 1, 1) and Date(2014, 12, 31)
define DateDurationBetweenExpression: days between Date(2014, 1, 1) and Date(2014, 12, 31)
define DateDurationInBetweenExpression: duration in days between Date(2014, 1, 1) and Date(2014, 12, 31)
define DateDifferenceExpression: difference in days of Interval[Date(2014, 1, 1), Date(2014, 12, 31)]
define DateDurationExpression: duration in days of Interval[Date(2014, 1, 1), Date(2014, 12, 31)]
define DateSameAsExpression: Date(2014, 1, 1) same day as Date(2014, 1, 1)
define DateSameOrAfterExpression: Date(2014, 1, 1) same day or after Date(2014, 1, 1)
define DateSameOrBeforeExpression: Date(2014, 1, 1) same day or before Date(2014, 1, 1)
define DateSubtract: Date(2014, 1, 1) - 1 day
define TodayExpression: Today()

// TimeOperators
define TimeAdd: Time(10, 30, 0) + 1 hour
define TimeAfter: Time(10, 30, 0) after Time(10, 0, 0)
define TimeBefore: Time(10, 0, 0) before Time(10, 30, 0)
define TimeHour: Time(0)
define TimeMinute: Time(0, 0)
define TimeSecond: Time(0, 0, 0)
define TimeMillisecond: Time(0, 0, 0, 0)
define TimeMillisecondOffset: Time(0, 0, 0, 0, 5.5)
define TimeTimezoneExtractor: timezone from Time(14, 30, 0, 0)
define HourFromTime: hour from Time(0, 0, 0, 0)
define MinuteFromTime: minute from Time(0, 0, 0, 0)
define SecondFromTime: second from Time(0, 0, 0, 0)
define MillisecondFromTime: millisecond from Time(0, 0, 0, 0)
define TimeDifferenceBetweenExpression: difference in hours between Time(10, 30, 0) and Time(11, 30, 0)
define TimeDurationBetweenExpression: hours between Time(10, 30, 0) and Time(11, 30, 0)
define TimeDurationInBetweenExpression: duration in hours between Time(10, 30, 0) and Time(11, 30, 0)
define TimeDifferenceExpression: difference in hours of Interval[Time(10, 30, 0), Time(11, 30, 0)]
define TimeDurationExpression: duration in hours of Interval[Time(10, 30, 0), Time(11, 30, 0)]
define TimeSameAsExpression: Time(10, 0, 0) same hour as Time(10, 30, 0)
define TimeSameOrAfterExpression: Time(10, 0, 0) same hour or after Time(10, 30, 0)
define TimeSameOrBeforeExpression: Time(10, 0, 0) same hour or before Time(10, 30, 0)
define TimeSubtract: Time(10, 30, 0) - 1 hour
define TimeOfDayExpression: TimeOfDay()
