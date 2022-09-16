using QDM version '5.3'

context Patient

define TestAgeInYears: AgeInYears()
define TestAgeInMonths: AgeInMonths()
define TestAgeInWeeks: AgeInWeeks()
define TestAgeInDays: AgeInDays()
define TestAgeInHours: AgeInHours()
define TestAgeInMinutes: AgeInMinutes()
define TestAgeInSeconds: AgeInSeconds()

define TestDate: @2012-01-01
define TestDateTime: @2012-01-01T00:00:00.0

define TestAgeInYearsAtDateTime: AgeInYearsAt(TestDateTime)
define TestAgeInYearsAtDate: AgeInYearsAt(TestDate)
define TestAgeInMonthsAtDateTime: AgeInMonthsAt(TestDateTime)
define TestAgeInMonthsAtDate: AgeInMonthsAt(TestDate)
define TestAgeInWeeksAtDateTime: AgeInWeeksAt(TestDateTime)
define TestAgeInWeeksAtDate: AgeInWeeksAt(TestDate)
define TestAgeInDaysAtDateTime: AgeInDaysAt(TestDateTime)
define TestAgeInDaysAtDate: AgeInDaysAt(TestDate)
define TestAgeInHoursAtDateTime: AgeInHoursAt(TestDateTime)
define TestAgeInMinutesAtDateTime: AgeInMinutesAt(TestDateTime)
define TestAgeInSecondsAtDateTime: AgeInSecondsAt(TestDateTime)

define TestCalculateAgeInYearsDateTime: CalculateAgeInYears(TestDateTime)
define TestCalculateAgeInYearsDate: CalculateAgeInYears(TestDate)
define TestCalculateAgeInMonthsDateTime: CalculateAgeInMonths(TestDateTime)
define TestCalculateAgeInMonthsDate: CalculateAgeInMonths(TestDate)
define TestCalculateAgeInWeeksDateTime: CalculateAgeInWeeks(TestDateTime)
define TestCalculateAgeInWeeksDate: CalculateAgeInWeeks(TestDate)
define TestCalculateAgeInDaysDateTime: CalculateAgeInDays(TestDateTime)
define TestCalculateAgeInDaysDate: CalculateAgeInDays(TestDate)
define TestCalculateAgeInHoursDateTime: CalculateAgeInHours(TestDateTime)
define TestCalculateAgeInMinutesDateTime: CalculateAgeInMinutes(TestDateTime)
define TestCalculateAgeInSecondsDateTime: CalculateAgeInSeconds(TestDateTime)

define TestCalculateAgeInYearsAtDateTime: CalculateAgeInYearsAt(TestDateTime, TestDateTime)
define TestCalculateAgeInYearsAtDate: CalculateAgeInYearsAt(TestDate, TestDate)
define TestCalculateAgeInMonthsAtDateTime: CalculateAgeInMonthsAt(TestDateTime, TestDateTime)
define TestCalculateAgeInMonthsAtDate: CalculateAgeInMonthsAt(TestDate, TestDate)
define TestCalculateAgeInWeeksAtDateTime: CalculateAgeInWeeksAt(TestDateTime, TestDateTime)
define TestCalculateAgeInWeeksAtDate: CalculateAgeInWeeksAt(TestDate, TestDate)
define TestCalculateAgeInDaysAtDateTime: CalculateAgeInDaysAt(TestDateTime, TestDateTime)
define TestCalculateAgeInDaysAtDate: CalculateAgeInDaysAt(TestDate, TestDate)
define TestCalculateAgeInHoursAtDateTime: CalculateAgeInHoursAt(TestDateTime, TestDateTime)
define TestCalculateAgeInMinutesAtDateTime: CalculateAgeInMinutesAt(TestDateTime, TestDateTime)
define TestCalculateAgeInSecondsAtDateTime: CalculateAgeInSecondsAt(TestDateTime, TestDateTime)

