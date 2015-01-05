// @Test: DateTime
define Year = DateTime(2012)
define Month = DateTime(2012, 2)
define Day = DateTime(2012, 2, 15)
define Hour = DateTime(2012, 2, 15, 12)
define Minute = DateTime(2012, 2, 15, 12, 10)
define Second = DateTime(2012, 2, 15, 12, 10, 59)
define Millisecond = DateTime(2012, 2, 15, 12, 10, 59, 456)
define TimezoneOffset = DateTime(2012, 2, 15, 12, 10, 59, 456, -8)

// @Test: Today
define TodayVar = Today()

// @Test: Now
define NowVar = Now()

// @Test: DateTimeComponentFrom
define IdesOfMarch = DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define Year = year from IdesOfMarch
define Month = month from IdesOfMarch
define Day = day from IdesOfMarch
define Hour = hour from IdesOfMarch
define Minute = minute from IdesOfMarch
define Second = second from IdesOfMarch
define Millisecond = millisecond from IdesOfMarch
define ImpreciseIdesOfMarch = DateTime(2000, 3, 15)
define ImpreciseComponentTuple = tuple {
  Year: year from ImpreciseIdesOfMarch,
  Month: month from ImpreciseIdesOfMarch,
  Day: day from ImpreciseIdesOfMarch,
  Hour: hour from ImpreciseIdesOfMarch,
  Minute: minute from ImpreciseIdesOfMarch,
  Second: second from ImpreciseIdesOfMarch,
  Millisecond: millisecond from ImpreciseIdesOfMarch
}
define NullDate = year from null

// @Test: DateFrom
define Date = date from DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define ImpreciseDate = date from DateTime(2000)
define NullDate = date from null

// @Test: TimeFrom
define Time = time from DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define NoTime = time from DateTime(2000, 3, 15)
define NullDate = time from null

// @Test: TimezoneFrom
define CentralEuropean = timezone from DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define EasternStandard = timezone from DateTime(2000, 3, 15, 13, 30, 25, 200, -5)
define DefaultTimezone = timezone from DateTime(2000, 3, 15, 13, 30, 25, 200)
define NullDate = timezone from null

// @Test: SameAs
define SameYear = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same year as DateTime(2000, 11, 23, 8, 14, 47, 500, +1)
define NotSameYear = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same year as DateTime(2001, 11, 23, 8, 14, 47, 500, +1)
define SameMonth = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month as DateTime(2000, 3, 23, 8, 14, 47, 500, +1)
define NotSameMonth = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month as DateTime(2000, 4, 23, 8, 14, 47, 500, +1)
define SameMonthWrongYear = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month as DateTime(2001, 3, 23, 8, 14, 47, 500, +1)
define SameDay = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day as DateTime(2000, 3, 15, 8, 14, 47, 500, +1)
define NotSameDay = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day as DateTime(2000, 3, 23, 8, 14, 47, 500, +1)
define SameDayWrongMonth = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day as DateTime(2000, 4, 15, 8, 14, 47, 500, +1)
define SameHour = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour as DateTime(2000, 3, 15, 13, 14, 47, 500, +1)
define NotSameHour = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour as DateTime(2000, 3, 15, 8, 14, 47, 500, +1)
define SameHourWrongDay = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour as DateTime(2000, 3, 16, 13, 14, 47, 500, +1)
define SameMinute = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute as DateTime(2000, 3, 15, 13, 30, 47, 500, +1)
define NotSameMinute = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute as DateTime(2000, 3, 15, 13, 14, 47, 500, +1)
define SameMinuteWrongHour = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute as DateTime(2000, 3, 15, 14, 30, 47, 500, +1)
define SameSecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second as DateTime(2000, 3, 15, 13, 30, 25, 500, +1)
define NotSameSecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second as DateTime(2000, 3, 15, 13, 30, 47, 500, +1)
define SameSecondWrongMinute = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second as DateTime(2000, 3, 15, 13, 31, 25, 500, +1)
define SameMillisecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond as DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define NotSameMillisecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond as DateTime(2000, 3, 15, 13, 30, 25, 500, +1)
define SameMillisecondWrongSecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond as DateTime(2000, 3, 15, 13, 30, 26, 200, +1)
define Same = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same as DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define NotSame = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same as DateTime(2000, 3, 15, 13, 30, 25, 500, +1)
define SameNormalized = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same as DateTime(2000, 3, 15, 7, 30, 25, 200, -5)
define SameHourWrongTimezone = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour as DateTime(2000, 3, 15, 13, 30, 25, 200, -5)
define ImpreciseHour = DateTime(2000, 3, 15, 13, 30, 25, 200) same hour as DateTime(2000, 3, 15)
define ImpreciseHourWrongDay = DateTime(2000, 3, 15, 13, 30, 25, 200) same hour as DateTime(2000, 3, 16)
define NullLeft = null same as DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define NullRight = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same as null
define NullBoth = null same as null

// @Test: SameOrAfter
define SameYear = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same year or after DateTime(2000, 11, 23, 8, 14, 47, 500, +1)
define YearAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same year or after DateTime(1999, 11, 23, 8, 14, 47, 500, +1)
define YearBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same year or after DateTime(2001, 11, 23, 8, 14, 47, 500, +1)
define SameMonth = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month or after DateTime(2000, 3, 23, 8, 14, 47, 500, +1)
define MonthAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month or after DateTime(2000, 2, 23, 8, 14, 47, 500, +1)
define MonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month or after DateTime(2000, 4, 23, 8, 14, 47, 500, +1)
define SameDay = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or after DateTime(2000, 3, 15, 8, 14, 47, 500, +1)
define DayAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or after DateTime(2000, 3, 14, 8, 14, 47, 500, +1)
define DayBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or after DateTime(2000, 3, 16, 8, 14, 47, 500, +1)
define SameHour = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or after DateTime(2000, 3, 15, 13, 14, 47, 500, +1)
define HourAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or after DateTime(2000, 3, 15, 12, 14, 47, 500, +1)
define HourBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or after DateTime(2000, 3, 15, 14, 14, 47, 500, +1)
define SameMinute = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute or after DateTime(2000, 3, 15, 13, 30, 47, 500, +1)
define MinuteAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute or after DateTime(2000, 3, 15, 13, 29, 47, 500, +1)
define MinuteBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute or after DateTime(2000, 3, 15, 13, 31, 47, 500, +1)
define SameSecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second or after DateTime(2000, 3, 15, 13, 30, 25, 500, +1)
define SecondAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second or after DateTime(2000, 3, 15, 13, 30, 24, 500, +1)
define SecondBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second or after DateTime(2000, 3, 15, 13, 30, 26, 500, +1)
define SameMillisecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define MillisecondAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 199, +1)
define MillisecondBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 201, +1)
define Same = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define After = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same or after DateTime(2000, 3, 15, 13, 30, 25, 199, +1)
define Before = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same or after DateTime(2000, 3, 15, 13, 30, 25, 201, +1)
define SameDayMonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or after DateTime(2000, 4, 15, 8, 14, 47, 500, +1)
define DayAfterMonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or after DateTime(2000, 4, 14, 8, 14, 47, 500, +1)
define DayBeforeMonthAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or after DateTime(2000, 2, 16, 8, 14, 47, 500, +1)
define ImpreciseDay = DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 3)
define ImpreciseDayMonthAfter = DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 2)
define ImpreciseDayMonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 4)
define SameHourNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or after DateTime(2000, 3, 15, 7, 30, 25, 200, -5)
define HourAfterNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or after DateTime(2000, 3, 15, 6, 30, 25, 200, -5)
define HourBeforeNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or after DateTime(2000, 3, 15, 8, 30, 25, 200, -5)
define NullLeft = null same or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define NullRight = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same or after null
define NullBoth = null same or after null

// @Test: SameOrBefore
define SameYear = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same year or before DateTime(2000, 11, 23, 8, 14, 47, 500, +1)
define YearAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same year or before DateTime(1999, 11, 23, 8, 14, 47, 500, +1)
define YearBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same year or before DateTime(2001, 11, 23, 8, 14, 47, 500, +1)
define SameMonth = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month or before DateTime(2000, 3, 23, 8, 14, 47, 500, +1)
define MonthAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month or before DateTime(2000, 2, 23, 8, 14, 47, 500, +1)
define MonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same month or before DateTime(2000, 4, 23, 8, 14, 47, 500, +1)
define SameDay = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or before DateTime(2000, 3, 15, 8, 14, 47, 500, +1)
define DayAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or before DateTime(2000, 3, 14, 8, 14, 47, 500, +1)
define DayBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or before DateTime(2000, 3, 16, 8, 14, 47, 500, +1)
define SameHour = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or before DateTime(2000, 3, 15, 13, 14, 47, 500, +1)
define HourAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or before DateTime(2000, 3, 15, 12, 14, 47, 500, +1)
define HourBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or before DateTime(2000, 3, 15, 14, 14, 47, 500, +1)
define SameMinute = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute or before DateTime(2000, 3, 15, 13, 30, 47, 500, +1)
define MinuteAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute or before DateTime(2000, 3, 15, 13, 29, 47, 500, +1)
define MinuteBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same minute or before DateTime(2000, 3, 15, 13, 31, 47, 500, +1)
define SameSecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second or before DateTime(2000, 3, 15, 13, 30, 25, 500, +1)
define SecondAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second or before DateTime(2000, 3, 15, 13, 30, 24, 500, +1)
define SecondBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same second or before DateTime(2000, 3, 15, 13, 30, 26, 500, +1)
define SameMillisecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define MillisecondAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 199, +1)
define MillisecondBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 201, +1)
define Same = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define After = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same or before DateTime(2000, 3, 15, 13, 30, 25, 199, +1)
define Before = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same or before DateTime(2000, 3, 15, 13, 30, 25, 201, +1)
define SameDayMonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or before DateTime(2000, 4, 15, 8, 14, 47, 500, +1)
define DayAfterMonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or before DateTime(2000, 4, 14, 8, 14, 47, 500, +1)
define DayBeforeMonthAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same day or before DateTime(2000, 2, 16, 8, 14, 47, 500, +1)
define ImpreciseDay = DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 3)
define ImpreciseDayMonthAfter = DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 2)
define ImpreciseDayMonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 4)
define SameHourNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or before DateTime(2000, 3, 15, 7, 30, 25, 200, -5)
define HourAfterNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or before DateTime(2000, 3, 15, 6, 30, 25, 200, -5)
define HourBeforeNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same hour or before DateTime(2000, 3, 15, 8, 30, 25, 200, -5)
define NullLeft = null same or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define NullRight = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) same or before null
define NullBoth = null same or before null

// @Test: After
define SameYear = DateTime(2000, 12, 15, 13, 30, 25, 200, +1) after year of DateTime(2000, 11, 23, 8, 14, 47, 500, +1)
define YearAfter = DateTime(2000, 12, 15, 13, 30, 25, 200, +1) after year of DateTime(1999, 11, 23, 8, 14, 47, 500, +1)
define YearBefore = DateTime(2000, 12, 15, 13, 30, 25, 200, +1) after year of DateTime(2001, 11, 23, 8, 14, 47, 500, +1)
define SameMonth = DateTime(2000, 3, 25, 13, 30, 25, 200, +1) after month of DateTime(2000, 3, 23, 8, 14, 47, 500, +1)
define MonthAfter = DateTime(2000, 3, 25, 13, 30, 25, 200, +1) after month of DateTime(2000, 2, 23, 8, 14, 47, 500, +1)
define MonthBefore = DateTime(2000, 3, 25, 13, 30, 25, 200, +1) after month of DateTime(2000, 4, 23, 8, 14, 47, 500, +1)
define SameDay = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after day of DateTime(2000, 3, 15, 8, 14, 47, 500, +1)
define DayAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after day of DateTime(2000, 3, 14, 8, 14, 47, 500, +1)
define DayBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after day of DateTime(2000, 3, 16, 8, 14, 47, 500, +1)
define SameHour = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1)
define HourAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after hour of DateTime(2000, 3, 15, 12, 14, 47, 500, +1)
define HourBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after hour of DateTime(2000, 3, 15, 14, 14, 47, 500, +1)
define SameMinute = DateTime(2000, 3, 15, 13, 30, 55, 200, +1) after minute of DateTime(2000, 3, 15, 13, 30, 47, 500, +1)
define MinuteAfter = DateTime(2000, 3, 15, 13, 30, 55, 200, +1) after minute of DateTime(2000, 3, 15, 13, 29, 47, 500, +1)
define MinuteBefore = DateTime(2000, 3, 15, 13, 30, 55, 200, +1) after minute of DateTime(2000, 3, 15, 13, 31, 47, 500, +1)
define SameSecond = DateTime(2000, 3, 15, 13, 30, 25, 700, +1) after second of DateTime(2000, 3, 15, 13, 30, 25, 500, +1)
define SecondAfter = DateTime(2000, 3, 15, 13, 30, 25, 700, +1) after second of DateTime(2000, 3, 15, 13, 30, 24, 500, +1)
define SecondBefore = DateTime(2000, 3, 15, 13, 30, 25, 700, +1) after second of DateTime(2000, 3, 15, 13, 30, 26, 500, +1)
define SameMillisecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define MillisecondAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 199, +1)
define MillisecondBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 201, +1)
define Same = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define After = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after DateTime(2000, 3, 15, 13, 30, 25, 199, +1)
define Before = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after DateTime(2000, 3, 15, 13, 30, 25, 201, +1)
define ImpreciseDay = DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 3)
define ImpreciseDayMonthAfter = DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 2)
define ImpreciseDayMonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 4)
define SameHourNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after hour of DateTime(2000, 3, 15, 7, 30, 25, 200, -5)
define HourAfterNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after hour of DateTime(2000, 3, 15, 6, 30, 25, 200, -5)
define HourBeforeNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after hour of DateTime(2000, 3, 15, 8, 30, 25, 200, -5)
define NullLeft = null after DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define NullRight = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) after null
define NullBoth = null after null

// @Test: Before
define SameYear = DateTime(2000, 10, 15, 13, 30, 25, 200, +1) before year of DateTime(2000, 11, 23, 8, 14, 47, 500, +1)
define YearAfter = DateTime(2000, 10, 15, 13, 30, 25, 200, +1) before year of DateTime(1999, 11, 23, 8, 14, 47, 500, +1)
define YearBefore = DateTime(2000, 10, 15, 13, 30, 25, 200, +1) before year of DateTime(2001, 11, 23, 8, 14, 47, 500, +1)
define SameMonth = DateTime(2000, 3, 22, 13, 30, 25, 200, +1) before month of DateTime(2000, 3, 23, 8, 14, 47, 500, +1)
define MonthAfter = DateTime(2000, 3, 22, 13, 30, 25, 200, +1) before month of DateTime(2000, 2, 23, 8, 14, 47, 500, +1)
define MonthBefore = DateTime(2000, 3, 22, 13, 30, 25, 200, +1) before month of DateTime(2000, 4, 23, 8, 14, 47, 500, +1)
define SameDay = DateTime(2000, 3, 15, 6, 30, 25, 200, +1) before day of DateTime(2000, 3, 15, 8, 14, 47, 500, +1)
define DayAfter = DateTime(2000, 3, 15, 6, 30, 25, 200, +1) before day of DateTime(2000, 3, 14, 8, 14, 47, 500, +1)
define DayBefore = DateTime(2000, 3, 15, 6, 30, 25, 200, +1) before day of DateTime(2000, 3, 16, 8, 14, 47, 500, +1)
define SameHour = DateTime(2000, 3, 15, 13, 10, 25, 200, +1) before hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1)
define HourAfter = DateTime(2000, 3, 15, 13, 10, 25, 200, +1) before hour of DateTime(2000, 3, 15, 12, 14, 47, 500, +1)
define HourBefore = DateTime(2000, 3, 15, 13, 10, 25, 200, +1) before hour of DateTime(2000, 3, 15, 14, 14, 47, 500, +1)
define SameMinute = DateTime(2000, 3, 15, 13, 30, 44, 200, +1) before minute of DateTime(2000, 3, 15, 13, 30, 47, 500, +1)
define MinuteAfter = DateTime(2000, 3, 15, 13, 30, 44, 200, +1) before minute of DateTime(2000, 3, 15, 13, 29, 47, 500, +1)
define MinuteBefore = DateTime(2000, 3, 15, 13, 30, 44, 200, +1) before minute of DateTime(2000, 3, 15, 13, 31, 47, 500, +1)
define SameSecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before second of DateTime(2000, 3, 15, 13, 30, 25, 500, +1)
define SecondAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before second of DateTime(2000, 3, 15, 13, 30, 24, 500, +1)
define SecondBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before second of DateTime(2000, 3, 15, 13, 30, 26, 500, +1)
define SameMillisecond = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define MillisecondAfter = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 199, +1)
define MillisecondBefore = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 201, +1)
define Same = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define After = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before DateTime(2000, 3, 15, 13, 30, 25, 199, +1)
define Before = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before DateTime(2000, 3, 15, 13, 30, 25, 201, +1)
define ImpreciseDay = DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 3)
define ImpreciseDayMonthAfter = DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 2)
define ImpreciseDayMonthBefore = DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 4)
define SameHourNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before hour of DateTime(2000, 3, 15, 7, 30, 25, 200, -5)
define HourAfterNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before hour of DateTime(2000, 3, 15, 6, 30, 25, 200, -5)
define HourBeforeNormalizeZones = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before hour of DateTime(2000, 3, 15, 8, 30, 25, 200, -5)
define NullLeft = null before DateTime(2000, 3, 15, 13, 30, 25, 200, +1)
define NullRight = DateTime(2000, 3, 15, 13, 30, 25, 200, +1) before null
define NullBoth = null before null

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
