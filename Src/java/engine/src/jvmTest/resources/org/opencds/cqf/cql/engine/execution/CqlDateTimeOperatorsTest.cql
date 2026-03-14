library CqlDateTimeOperatorsTest

//Duration
define DateTimeDurationBetweenYear: years between DateTime(2005) and DateTime(2010)

// Uncertainty tests
define DateTimeDurationBetweenUncertainInterval: days between DateTime(2014, 1, 15) and DateTime(2014, 2)
define DateTimeDurationBetweenUncertainInterval2: months between DateTime(2005) and DateTime(2006, 5)
define DateTimeDurationBetweenUncertainAdd: DateTimeDurationBetweenUncertainInterval + DateTimeDurationBetweenUncertainInterval
define DateTimeDurationBetweenUncertainSubtract: DateTimeDurationBetweenUncertainInterval - DateTimeDurationBetweenUncertainInterval2
define DateTimeDurationBetweenUncertainMultiply: DateTimeDurationBetweenUncertainInterval * DateTimeDurationBetweenUncertainInterval

define DurationInDaysA: days between @2017-03-12T00:00:00-07:00 and @2017-03-13T00:00:00-06:00 // Should be 1
define DurationInDaysAA: days between DateTime(2017, 3, 12, 0, 0, 0, 0, -7.0) and DateTime(2017, 3, 13, 0, 0, 0, 0, -6.0) // Should be 1

//Now
define Issue34A: Now()

//TimeOfDay
define TimeOfDayTest: TimeOfDay()

//Today
define Issue34B: Today()

// Default timezoneOffset
define Issue1420: hours between DateTime(2024, 10, 3, 15, 54, 0, 0) and DateTime(2024, 10, 3, 15, 54, 0, 0, 0)