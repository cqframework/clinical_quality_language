library TimeOperators version '2'

define TimeAdd: Time(10, 15, 30) + 1 hour
define TimeAfter: Time(10, 15, 30) after Time(10, 00, 00)
define TimeBefore: Time(10, 15, 30) before Time(10, 30, 30)
define TimeHour: Time(0)
define TimeMinute: Time(0, 0)
define TimeSecond: Time(0, 0, 0)
define TimeMillisecond: Time(0, 0, 0, 0)
define TimezoneExtractor: timezone from Time(14, 30, 0, 0)
define HourFromTime: hour from Time(0, 0, 0, 0)
define MinuteFromTime: minute from Time(0, 0, 0, 0)
define SecondFromTime: second from Time(0, 0, 0, 0)
define MillisecondFromTime: millisecond from Time(0, 0, 0, 0)
define DifferenceBetweenExpression: hours between Time(10, 0, 0) and Time(12, 15, 45)
define DurationBetweenExpression: hours between Time(10, 0, 0) and Time(12, 15, 45)
define TimeOfDayExpression: TimeOfDay()
define SameAsExpression: Time(10, 30, 0) same hour as Time(10, 45, 0)
define SameOrAfterExpression: Time(10, 30, 0) same hour or after Time(9, 30, 0)
define SameOrBeforeExpression: Time(10, 30, 0) same hour or before Time(11, 30, 0)
define TimeSubtract: Time(10, 30, 0) - 1 hour
define TimeOfDayExpression: TimeOfDay()