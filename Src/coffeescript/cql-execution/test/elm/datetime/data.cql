// @Test: DateTimeFunctionRef
define Year = DateTime(2012)
define Month = DateTime(2012, 4)
define Day = DateTime(2012, 4, 15)
define Hour = DateTime(2012, 4, 15, 12)
define Minute = DateTime(2012, 4, 15, 12, 10)
define Second = DateTime(2012, 4, 15, 12, 10, 59)
define Millisecond = DateTime(2012, 4, 15, 12, 10, 59, 456)
define TimeZoneOffset = DateTime(2012, 4, 15, 12, 10, 59, 456, -5)

// @Test: DurationBetween
define NewYear2013 = DateTime(2013, 1, 1, 0, 0, 0, 0)
define NewYear2014 = DateTime(2014, 1, 1, 0, 0, 0, 0)
define January2014 = DateTime(2014, 1)
define YearsBetween = years between NewYear2013 and NewYear2014
define MonthsBetween = months between NewYear2013 and NewYear2014
define DaysBetween = days between NewYear2013 and NewYear2014
define HoursBetween = hours between NewYear2013 and NewYear2014
define MinutesBetween = minutes between NewYear2013 and NewYear2014
define SecondsBetween = seconds between NewYear2013 and NewYear2014
define MillisecondsBetween = milliseconds between NewYear2013 and NewYear2014
define MillisecondsBetweenReversed = milliseconds between NewYear2014 and NewYear2013
define YearsBetweenUncertainty = years between NewYear2014 and January2014
define MonthsBetweenUncertainty = months between NewYear2014 and January2014
define DaysBetweenUncertainty = days between NewYear2014 and January2014
define HoursBetweenUncertainty = hours between NewYear2014 and January2014
define MinutesBetweenUncertainty = minutes between NewYear2014 and January2014
define SecondsBetweenUncertainty = seconds between NewYear2014 and January2014
define MillisecondsBetweenUncertainty = milliseconds between NewYear2014 and January2014
define MillisecondsBetweenReversedUncertainty = milliseconds between January2014 and NewYear2014

// @Test: DurationBetween Comparisons
define NewYear2014 = DateTime(2014, 1, 1, 0, 0, 0, 0)
define February2014 = DateTime(2014, 2)
define GreaterThan25DaysAfter = days between NewYear2014 and February2014 > 25
define GreaterThan40DaysAfter = days between NewYear2014 and February2014 > 40
define GreaterThan80DaysAfter = days between NewYear2014 and February2014 > 80
define GreaterOrEqualTo25DaysAfter = days between NewYear2014 and February2014 >= 25
define GreaterOrEqualTo40DaysAfter = days between NewYear2014 and February2014 >= 40
define GreaterOrEqualTo80DaysAfter = days between NewYear2014 and February2014 >= 80
define EqualTo25DaysAfter = days between NewYear2014 and February2014 = 25
define EqualTo40DaysAfter = days between NewYear2014 and February2014 = 40
define EqualTo80DaysAfter = days between NewYear2014 and February2014 = 80
define LessOrEqualTo25DaysAfter = days between NewYear2014 and February2014 <= 25
define LessOrEqualTo40DaysAfter = days between NewYear2014 and February2014 <= 40
define LessOrEqualTo80DaysAfter = days between NewYear2014 and February2014 <= 80
define LessThan25DaysAfter = days between NewYear2014 and February2014 < 25
define LessThan40DaysAfter = days between NewYear2014 and February2014 < 40
define LessThan80DaysAfter = days between NewYear2014 and February2014 < 80
define TwentyFiveDaysLessThanDaysBetween = 25 < days between NewYear2014 and February2014
define FortyDaysEqualToDaysBetween = 40 = days between NewYear2014 and February2014
define TwentyFiveDaysGreaterThanDaysBetween = 25 > days between NewYear2014 and February2014
