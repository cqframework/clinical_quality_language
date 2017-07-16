// @Test: DateTime
define Year: DateTime(2012)
define Month: DateTime(2012, 2)
define Day: DateTime(2012, 2, 15)
define Hour: DateTime(2012, 2, 15, 12)
define Minute: DateTime(2012, 2, 15, 12, 10)
define Second: DateTime(2012, 2, 15, 12, 10, 59)
define Millisecond: DateTime(2012, 2, 15, 12, 10, 59, 456)
define TimezoneOffset: DateTime(2012, 2, 15, 12, 10, 59, 456, -8.0)

// @Test: Time
define Hour: Time(12)
define Minute: Time(12, 10)
define Second: Time(12, 10, 59)
define Millisecond: Time(12, 10, 59, 456)
define TimezoneOffset: Time(12, 10, 59, 456, -8.0)

// @Test: Today
define TodayVar: Today()

// @Test: Now
define NowVar: Now()

// @Test: TimeOfDay
define TimeOfDayVar: TimeOfDay()

// @Test: DateTimeComponentFrom
define IdesOfMarch: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define Year: year from IdesOfMarch
define Month: month from IdesOfMarch
define Day: day from IdesOfMarch
define Hour: hour from IdesOfMarch
define Minute: minute from IdesOfMarch
define Second: second from IdesOfMarch
define Millisecond: millisecond from IdesOfMarch
define ImpreciseIdesOfMarch: DateTime(2000, 3, 15)
define ImpreciseComponentTuple: Tuple {
  Year: year from ImpreciseIdesOfMarch,
  Month: month from ImpreciseIdesOfMarch,
  Day: day from ImpreciseIdesOfMarch,
  Hour: hour from ImpreciseIdesOfMarch,
  Minute: minute from ImpreciseIdesOfMarch,
  Second: second from ImpreciseIdesOfMarch,
  Millisecond: millisecond from ImpreciseIdesOfMarch
}
define NullDate: year from (null as DateTime)

// @Test: DateFrom
define Date: date from DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define ImpreciseDate: date from DateTime(2000)
define NullDate: date from (null as DateTime)

// @Test: TimeFrom
define Time: time from DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NoTime: time from DateTime(2000, 3, 15)
define NullDate: time from null

// @Test: TimezoneFrom
define CentralEuropean: timezone from DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define EasternStandard: timezone from DateTime(2000, 3, 15, 13, 30, 25, 200, -5.0)
define DefaultTimezone: timezone from DateTime(2000, 3, 15, 13, 30, 25, 200)
define NullDate: timezone from (null as DateTime)

// @Test: SameAs
define SameYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year as DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define NotSameYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year as DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define SameMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month as DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define NotSameMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month as DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define SameMonthWrongYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month as DateTime(2001, 3, 23, 8, 14, 47, 500, +1.0)
define SameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day as DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define NotSameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day as DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define SameDayWrongMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day as DateTime(2000, 4, 15, 8, 14, 47, 500, +1.0)
define SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define NotSameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define SameHourWrongDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 16, 13, 14, 47, 500, +1.0)
define SameMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute as DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define NotSameMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute as DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define SameMinuteWrongHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute as DateTime(2000, 3, 15, 14, 30, 47, 500, +1.0)
define SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second as DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define NotSameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second as DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define SameSecondWrongMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second as DateTime(2000, 3, 15, 13, 31, 25, 500, +1.0)
define SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond as DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NotSameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond as DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SameMillisecondWrongSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond as DateTime(2000, 3, 15, 13, 30, 26, 200, +1.0)
define Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same as DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NotSame: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same as DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SameNormalized: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same as DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define SameHourWrongTimezone: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour as DateTime(2000, 3, 15, 13, 30, 25, 200, -5.0)
define ImpreciseHour: DateTime(2000, 3, 15, 13, 30, 25, 200) same hour as DateTime(2000, 3, 15)
define ImpreciseHourWrongDay: DateTime(2000, 3, 15, 13, 30, 25, 200) same hour as DateTime(2000, 3, 16)
define NullLeft: null same as DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same as (null as DateTime)
define NullBoth: (null as DateTime) same as null

// @Test: SameOrAfter
define SameYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or after DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define YearAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or after DateTime(1999, 11, 23, 8, 14, 47, 500, +1.0)
define YearBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or after DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define SameMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or after DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define MonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or after DateTime(2000, 2, 23, 8, 14, 47, 500, +1.0)
define MonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or after DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define SameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define DayAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 3, 14, 8, 14, 47, 500, +1.0)
define DayBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 3, 16, 8, 14, 47, 500, +1.0)
define SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define HourAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 12, 14, 47, 500, +1.0)
define HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or after DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define MinuteAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or after DateTime(2000, 3, 15, 13, 29, 47, 500, +1.0)
define MinuteBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or after DateTime(2000, 3, 15, 13, 31, 47, 500, +1.0)
define SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or after DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or after DateTime(2000, 3, 15, 13, 30, 24, 500, +1.0)
define SecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or after DateTime(2000, 3, 15, 13, 30, 26, 500, +1.0)
define SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define MillisecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define MillisecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or after DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define After: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or after DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define Before: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or after DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define SameDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 4, 15, 8, 14, 47, 500, +1.0)
define DayAfterMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 4, 14, 8, 14, 47, 500, +1.0)
define DayBeforeMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or after DateTime(2000, 2, 16, 8, 14, 47, 500, +1.0)
define ImpreciseDay: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 3)
define ImpreciseDayMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 2)
define ImpreciseDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or after DateTime(2000, 4)
define SameHourNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define HourAfterNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 6, 30, 25, 200, -5.0)
define HourBeforeNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or after DateTime(2000, 3, 15, 8, 30, 25, 200, -5.0)
define NullLeft: null same or after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or after null
define NullBoth: (null as DateTime) same or after null

// @Test: SameOrBefore
define SameYear: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or before DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define YearAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or before DateTime(1999, 11, 23, 8, 14, 47, 500, +1.0)
define YearBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same year or before DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define SameMonth: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or before DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define MonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or before DateTime(2000, 2, 23, 8, 14, 47, 500, +1.0)
define MonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same month or before DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define SameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define DayAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 3, 14, 8, 14, 47, 500, +1.0)
define DayBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 3, 16, 8, 14, 47, 500, +1.0)
define SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define HourAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 12, 14, 47, 500, +1.0)
define HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameMinute: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or before DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define MinuteAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or before DateTime(2000, 3, 15, 13, 29, 47, 500, +1.0)
define MinuteBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same minute or before DateTime(2000, 3, 15, 13, 31, 47, 500, +1.0)
define SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or before DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or before DateTime(2000, 3, 15, 13, 30, 24, 500, +1.0)
define SecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same second or before DateTime(2000, 3, 15, 13, 30, 26, 500, +1.0)
define SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define MillisecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define MillisecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same millisecond or before DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define After: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or before DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define Before: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or before DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define SameDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 4, 15, 8, 14, 47, 500, +1.0)
define DayAfterMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 4, 14, 8, 14, 47, 500, +1.0)
define DayBeforeMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same day or before DateTime(2000, 2, 16, 8, 14, 47, 500, +1.0)
define ImpreciseDay: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 3)
define ImpreciseDayMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 2)
define ImpreciseDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200) same day or before DateTime(2000, 4)
define SameHourNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define HourAfterNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 6, 30, 25, 200, -5.0)
define HourBeforeNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same hour or before DateTime(2000, 3, 15, 8, 30, 25, 200, -5.0)
define NullLeft: null same or before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) same or before null
define NullBoth: (null as DateTime) same or before null

// @Test: After
define SameYear: DateTime(2000, 12, 15, 13, 30, 25, 200, +1.0) after year of DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define YearAfter: DateTime(2000, 12, 15, 13, 30, 25, 200, +1.0) after year of DateTime(1999, 11, 23, 8, 14, 47, 500, +1.0)
define YearBefore: DateTime(2000, 12, 15, 13, 30, 25, 200, +1.0) after year of DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define SameMonth: DateTime(2000, 3, 25, 13, 30, 25, 200, +1.0) after month of DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define MonthAfter: DateTime(2000, 3, 25, 13, 30, 25, 200, +1.0) after month of DateTime(2000, 2, 23, 8, 14, 47, 500, +1.0)
define MonthBefore: DateTime(2000, 3, 25, 13, 30, 25, 200, +1.0) after month of DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define SameDay: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after day of DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define DayAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after day of DateTime(2000, 3, 14, 8, 14, 47, 500, +1.0)
define DayBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after day of DateTime(2000, 3, 16, 8, 14, 47, 500, +1.0)
define SameHour: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define HourAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 12, 14, 47, 500, +1.0)
define HourBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameMinute: DateTime(2000, 3, 15, 13, 30, 55, 200, +1.0) after minute of DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define MinuteAfter: DateTime(2000, 3, 15, 13, 30, 55, 200, +1.0) after minute of DateTime(2000, 3, 15, 13, 29, 47, 500, +1.0)
define MinuteBefore: DateTime(2000, 3, 15, 13, 30, 55, 200, +1.0) after minute of DateTime(2000, 3, 15, 13, 31, 47, 500, +1.0)
define SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 700, +1.0) after second of DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 700, +1.0) after second of DateTime(2000, 3, 15, 13, 30, 24, 500, +1.0)
define SecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 700, +1.0) after second of DateTime(2000, 3, 15, 13, 30, 26, 500, +1.0)
define SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define MillisecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define MillisecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after millisecond of DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define After: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define Before: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define ImpreciseDay: DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 3)
define ImpreciseDayMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 2)
define ImpreciseDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200) after day of DateTime(2000, 4)
define SameHourNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define HourAfterNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 6, 30, 25, 200, -5.0)
define HourBeforeNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after hour of DateTime(2000, 3, 15, 8, 30, 25, 200, -5.0)
define NullLeft: null after DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) after null
define NullBoth: (null as DateTime) after null

// @Test: Before
define SameYear: DateTime(2000, 10, 15, 13, 30, 25, 200, +1.0) before year of DateTime(2000, 11, 23, 8, 14, 47, 500, +1.0)
define YearAfter: DateTime(2000, 10, 15, 13, 30, 25, 200, +1.0) before year of DateTime(1999, 11, 23, 8, 14, 47, 500, +1.0)
define YearBefore: DateTime(2000, 10, 15, 13, 30, 25, 200, +1.0) before year of DateTime(2001, 11, 23, 8, 14, 47, 500, +1.0)
define SameMonth: DateTime(2000, 3, 22, 13, 30, 25, 200, +1.0) before month of DateTime(2000, 3, 23, 8, 14, 47, 500, +1.0)
define MonthAfter: DateTime(2000, 3, 22, 13, 30, 25, 200, +1.0) before month of DateTime(2000, 2, 23, 8, 14, 47, 500, +1.0)
define MonthBefore: DateTime(2000, 3, 22, 13, 30, 25, 200, +1.0) before month of DateTime(2000, 4, 23, 8, 14, 47, 500, +1.0)
define SameDay: DateTime(2000, 3, 15, 6, 30, 25, 200, +1.0) before day of DateTime(2000, 3, 15, 8, 14, 47, 500, +1.0)
define DayAfter: DateTime(2000, 3, 15, 6, 30, 25, 200, +1.0) before day of DateTime(2000, 3, 14, 8, 14, 47, 500, +1.0)
define DayBefore: DateTime(2000, 3, 15, 6, 30, 25, 200, +1.0) before day of DateTime(2000, 3, 16, 8, 14, 47, 500, +1.0)
define SameHour: DateTime(2000, 3, 15, 13, 10, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 13, 14, 47, 500, +1.0)
define HourAfter: DateTime(2000, 3, 15, 13, 10, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 12, 14, 47, 500, +1.0)
define HourBefore: DateTime(2000, 3, 15, 13, 10, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 14, 14, 47, 500, +1.0)
define SameMinute: DateTime(2000, 3, 15, 13, 30, 44, 200, +1.0) before minute of DateTime(2000, 3, 15, 13, 30, 47, 500, +1.0)
define MinuteAfter: DateTime(2000, 3, 15, 13, 30, 44, 200, +1.0) before minute of DateTime(2000, 3, 15, 13, 29, 47, 500, +1.0)
define MinuteBefore: DateTime(2000, 3, 15, 13, 30, 44, 200, +1.0) before minute of DateTime(2000, 3, 15, 13, 31, 47, 500, +1.0)
define SameSecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before second of DateTime(2000, 3, 15, 13, 30, 25, 500, +1.0)
define SecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before second of DateTime(2000, 3, 15, 13, 30, 24, 500, +1.0)
define SecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before second of DateTime(2000, 3, 15, 13, 30, 26, 500, +1.0)
define SameMillisecond: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define MillisecondAfter: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define MillisecondBefore: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before millisecond of DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define Same: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define After: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before DateTime(2000, 3, 15, 13, 30, 25, 199, +1.0)
define Before: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define ImpreciseDay: DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 3)
define ImpreciseDayMonthAfter: DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 2)
define ImpreciseDayMonthBefore: DateTime(2000, 3, 15, 13, 30, 25, 200) before day of DateTime(2000, 4)
define SameHourNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 7, 30, 25, 200, -5.0)
define HourAfterNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 6, 30, 25, 200, -5.0)
define HourBeforeNormalizeZones: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before hour of DateTime(2000, 3, 15, 8, 30, 25, 200, -5.0)
define NullLeft: null before DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define NullRight: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) before null
define NullBoth: (null as DateTime) before null

// @Test: DifferenceBetween
define NewYear2013: DateTime(2013, 1, 1, 0, 0, 0, 0)
define NewYear2014: DateTime(2014, 1, 1, 0, 0, 0, 0)
define January2014: DateTime(2014, 1)
define YearsBetween: difference in years between NewYear2013 and NewYear2014
define MonthsBetween: difference in months between NewYear2013 and NewYear2014
define WeeksBetween: difference in weeks between NewYear2013 and NewYear2014
define DaysBetween: difference in days between NewYear2013 and NewYear2014
define HoursBetween: difference in hours between NewYear2013 and NewYear2014
define MinutesBetween: difference in minutes between NewYear2013 and NewYear2014
define SecondsBetween: difference in seconds between NewYear2013 and NewYear2014
define MillisecondsBetween: difference in milliseconds between NewYear2013 and NewYear2014
define MillisecondsBetweenReversed: difference in milliseconds between NewYear2014 and NewYear2013
define YearsBetweenUncertainty: difference in years between NewYear2014 and January2014
define MonthsBetweenUncertainty: difference in months between NewYear2014 and January2014
define WeeksBetweenUncertainty: difference in weeks between NewYear2014 and January2014
define DaysBetweenUncertainty: difference in days between NewYear2014 and January2014
define HoursBetweenUncertainty: difference in hours between NewYear2014 and January2014
define MinutesBetweenUncertainty: difference in minutes between NewYear2014 and January2014
define SecondsBetweenUncertainty: difference in seconds between NewYear2014 and January2014
define MillisecondsBetweenUncertainty: difference in milliseconds between NewYear2014 and January2014
define MillisecondsBetweenReversedUncertainty: difference in milliseconds between January2014 and NewYear2014

// @Test: DifferenceBetween Comparisons
define NewYear2014: DateTime(2014, 1, 1, 0, 0, 0, 0)
define February2014: DateTime(2014, 2)
define GreaterThan25DaysAfter: difference in days between NewYear2014 and February2014 > 25
define GreaterThan40DaysAfter: difference in days between NewYear2014 and February2014 > 40
define GreaterThan80DaysAfter: difference in days between NewYear2014 and February2014 > 80
define GreaterOrEqualTo25DaysAfter: difference in days between NewYear2014 and February2014 >= 25
define GreaterOrEqualTo40DaysAfter: difference in days between NewYear2014 and February2014 >= 40
define GreaterOrEqualTo80DaysAfter: difference in days between NewYear2014 and February2014 >= 80
define EqualTo25DaysAfter: difference in days between NewYear2014 and February2014 = 25
define EqualTo40DaysAfter: difference in days between NewYear2014 and February2014 = 40
define EqualTo80DaysAfter: difference in days between NewYear2014 and February2014 = 80
define LessOrEqualTo25DaysAfter: difference in days between NewYear2014 and February2014 <= 25
define LessOrEqualTo40DaysAfter: difference in days between NewYear2014 and February2014 <= 40
define LessOrEqualTo80DaysAfter: difference in days between NewYear2014 and February2014 <= 80
define LessThan25DaysAfter: difference in days between NewYear2014 and February2014 < 25
define LessThan40DaysAfter: difference in days between NewYear2014 and February2014 < 40
define LessThan80DaysAfter: difference in days between NewYear2014 and February2014 < 80
define TwentyFiveDaysLessThanDaysBetween: 25 < difference in days between NewYear2014 and February2014
define FortyDaysEqualToDaysBetween: 40 = difference in days between NewYear2014 and February2014
define TwentyFiveDaysGreaterThanDaysBetween: 25 > difference in days between NewYear2014 and February2014

// @Test: DurationBetween
define DecTen2013: DateTime(2013, 12, 10, 12, 30, 30, 500)
define JanOne2015: DateTime(2015, 1, 1, 0, 0, 0, 0)
define January2015: DateTime(2015, 1)
define YearsBetween: years between DecTen2013 and JanOne2015
define MonthsBetween: months between DecTen2013 and JanOne2015
define WeeksBetween: weeks between DecTen2013 and JanOne2015
define DaysBetween: days between DecTen2013 and JanOne2015
define HoursBetween: hours between DecTen2013 and JanOne2015
define MinutesBetween: minutes between DecTen2013 and JanOne2015
define SecondsBetween: seconds between DecTen2013 and JanOne2015
define MillisecondsBetween: milliseconds between DecTen2013 and JanOne2015
define MillisecondsBetweenReversed: milliseconds between JanOne2015 and DecTen2013
define YearsBetweenUncertainty: years between JanOne2015 and January2015
define MonthsBetweenUncertainty: months between JanOne2015 and January2015
define WeeksBetweenUncertainty: weeks between JanOne2015 and January2015
define DaysBetweenUncertainty: days between JanOne2015 and January2015
define HoursBetweenUncertainty: hours between JanOne2015 and January2015
define MinutesBetweenUncertainty: minutes between JanOne2015 and January2015
define SecondsBetweenUncertainty: seconds between JanOne2015 and January2015
define MillisecondsBetweenUncertainty: milliseconds between JanOne2015 and January2015
define MillisecondsBetweenReversedUncertainty: milliseconds between January2015 and JanOne2015

// @Test: DateMath
define June15th2013: DateTime(2013, 6, 15, 0, 0, 0, 0)
define PlusThreeYears: June15th2013 + 3 years
define MinusThreeYears: June15th2013 - 3 years
define PlusEightMonths: June15th2013 + 8 months
define MinusEightMonths: June15th2013 - 8 months
define PlusThreeWeeks: June15th2013 + 3 weeks
define MinusThreeWeeks: June15th2013 - 3 weeks
define PlusTwentyDays: June15th2013 + 20 days
define MinusTwentyDays: June15th2013 - 20 days
define PlusThreeHours: June15th2013 + 3 hours
define MinusThreeHours: June15th2013 - 3 hours
define PlusThreeMinutes: June15th2013 + 3 minutes
define MinusThreeMinutes: June15th2013 - 3 minutes
define PlusThreeSeconds: June15th2013 + 3 seconds
define MinusThreeSeconds: June15th2013 - 3 seconds
define PlusThreeMilliseconds: June15th2013 + 3 milliseconds
define MinusThreeMilliseconds: June15th2013 - 3 milliseconds