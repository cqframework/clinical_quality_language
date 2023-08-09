package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlDateTimeOperatorsTest extends CqlTestBase {
    @Test
    public void test_all_date_time_tests() throws IOException {

        EvaluationResult evaluationResult;
        evaluationResult = engine.evaluate(toElmIdentifier("CqlDateTimeOperatorsTest"), ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()));

        Object result = evaluationResult.forExpression("DateTimeAdd5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2010, 10, 10)));

        result = evaluationResult.forExpression("DateTimeAdd5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 10, 10)));

        result = evaluationResult.forExpression("DateTimeAddMonthsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2006, 3, 10)));

        result = evaluationResult.forExpression("DateTimeAddThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2018, 5, 23)));

        result = evaluationResult.forExpression("DateTimeAddYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2019, 5, 23)));

        result = evaluationResult.forExpression("DateTimeAdd5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 15)));

        result = evaluationResult.forExpression("DateTimeAddDaysOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 7, 1)));

        result = evaluationResult.forExpression("DateTimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 10)));

        result = evaluationResult.forExpression("DateTimeAdd5HoursWithLeftMinPrecisionSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 10, 20, 30)));

        result = evaluationResult.forExpression("DateTimeAdd5HoursWithLeftMinPrecisionDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10)));

        result = evaluationResult.forExpression("DateTimeAdd5HoursWithLeftMinPrecisionDayOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 11)));

        result = evaluationResult.forExpression("DateAdd2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2016)));

        result = evaluationResult.forExpression("DateAdd2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2016)));

        result = evaluationResult.forExpression("DateAdd33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014,7)));

        result = evaluationResult.forExpression("DateAdd1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2015,6)));

        result = evaluationResult.forExpression("DateTimeAddHoursOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 11, 0)));

        result = evaluationResult.forExpression("DateTimeAdd5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 10)));

        result = evaluationResult.forExpression("DateTimeAddMinutesOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 6, 0)));

        result = evaluationResult.forExpression("DateTimeAdd5Seconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 10)));

        result = evaluationResult.forExpression("DateTimeAddSecondsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 6, 0)));

        result = evaluationResult.forExpression("DateTimeAdd5Milliseconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5, 10)));

        result = evaluationResult.forExpression("DateTimeAddMillisecondsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 5, 6, 0)));

        result = evaluationResult.forExpression("DateTimeAddLeapYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2013, 2, 28)));

        result = evaluationResult.forExpression("DateTimeAdd2YearsByMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.forExpression("DateTimeAdd2YearsByDays").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.forExpression("DateTimeAdd2YearsByDaysRem5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.forExpression("TimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = evaluationResult.forExpression("TimeAdd1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 59, 999)));

        result = evaluationResult.forExpression("TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 999)));

        result = evaluationResult.forExpression("TimeAdd1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 0)));

        result = evaluationResult.forExpression("TimeAdd5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(21, 0, 59, 999)));

        // checking access ordering and returning correct result
        result = evaluationResult.forExpression("TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 999)));

        result = evaluationResult.forExpression("TimeAdd5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = evaluationResult.forExpression("DateTimeAfterYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAfterMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAfterDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAfterHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAfterMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAfterSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAfterMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAfterUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeAfterHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeAfterMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeAfterSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeAfterMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeAfterTimeCstor").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeBeforeMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeBeforeDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeBeforeHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeBeforeMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeBeforeSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeBeforeMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("BeforeTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("BeforeTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeBeforeHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeBeforeMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeBeforeSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeBeforeMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003)));

        result = evaluationResult.forExpression("DateTimeMonth").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10)));

        result = evaluationResult.forExpression("DateTimeDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29)));

        result = evaluationResult.forExpression("DateTimeHour").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20)));

        result = evaluationResult.forExpression("DateTimeMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50)));

        result = evaluationResult.forExpression("DateTimeSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50, 33)));

        result = evaluationResult.forExpression("DateTimeMillisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50, 33, 955)));

        result = evaluationResult.forExpression("DateTimeComponentFromYear").value();
        assertThat(result, is(2003));

        result = evaluationResult.forExpression("DateTimeComponentFromMonth").value();
        assertThat(result, is(10));

        result = evaluationResult.forExpression("DateTimeComponentFromMonthMinBoundary").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DateTimeComponentFromDay").value();
        assertThat(result, is(29));

        result = evaluationResult.forExpression("DateTimeComponentFromHour").value();
        assertThat(result, is(20));

        result = evaluationResult.forExpression("DateTimeComponentFromMinute").value();
        assertThat(result, is(50));

        result = evaluationResult.forExpression("DateTimeComponentFromSecond").value();
        assertThat(result, is(33));

        result = evaluationResult.forExpression("DateTimeComponentFromMillisecond").value();
        assertThat(result, is(955));

        result = evaluationResult.forExpression("DateTimeComponentFromTimezone").value();
        assertThat(result, is(new BigDecimal("1.0")));

        result = evaluationResult.forExpression("DateTimeComponentFromDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2003, 10, 29)));

        result = evaluationResult.forExpression("TimeComponentFromHour").value();
        assertThat(result, is(23));

        result = evaluationResult.forExpression("TimeComponentFromMinute").value();
        assertThat(result, is(20));

        result = evaluationResult.forExpression("TimeComponentFromSecond").value();
        assertThat(result, is(15));

        result = evaluationResult.forExpression("TimeComponentFromMilli").value();
        assertThat(result, is(555));

        result = evaluationResult.forExpression("DateTimeDifferenceYear").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("DateTimeDifferenceMonth").value();
        assertThat(result, is(8));

        result = evaluationResult.forExpression("DateTimeDifferenceDay").value();
        assertThat(result, is(10));

        result = evaluationResult.forExpression("DateTimeDifferenceHour").value();
        assertThat(result, is(8));

        result = evaluationResult.forExpression("DateTimeDifferenceMinute").value();
        assertThat(result, is(9));

        result = evaluationResult.forExpression("DateTimeDifferenceSecond").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("DateTimeDifferenceMillisecond").value();
        assertThat(result, is(3600400));

        result = evaluationResult.forExpression("DateTimeDifferenceWeeks").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DateTimeDifferenceWeeks2").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("DateTimeDifferenceWeeks2").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("DateTimeDifferenceNegative").value();
        assertThat(result, is(-18));

        result = evaluationResult.forExpression("DateTimeDifferenceUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeDifferenceHour").value();
        assertThat(result, is(3));

        result = evaluationResult.forExpression("TimeDifferenceMinute").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("TimeDifferenceSecond").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("TimeDifferenceMillis").value();
        assertThat(result, is(-5));

        result = evaluationResult.forExpression("DifferenceInHoursA").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DifferenceInMinutesA").value();
        assertThat(result, is(45));

        result = evaluationResult.forExpression("DifferenceInDaysA").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DifferenceInHoursAA").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DifferenceInMinutesAA").value();
        assertThat(result, is(45));

        result = evaluationResult.forExpression("DifferenceInDaysAA").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DateTimeDurationBetweenYear").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("DurationInWeeks").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DurationInWeeks2").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DurationInWeeks3").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("DateTimeDurationBetweenYearOffset").value();
        assertThat(result, is(4));

        result = evaluationResult.forExpression("DateTimeDurationBetweenMonth").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("DateTimeDurationBetweenDaysDiffYears").value();
        assertThat(result, is(-788));

        result = evaluationResult.forExpression("DateTimeDurationBetweenUncertainInterval").value();
        Assert.assertTrue(((Interval)result).getStart().equals(17));
        Assert.assertTrue(((Interval)result).getEnd().equals(44));

        result = evaluationResult.forExpression("DateTimeDurationBetweenUncertainInterval2").value();
        Assert.assertTrue(((Interval)result).getStart().equals(5));
        Assert.assertTrue(((Interval)result).getEnd().equals(16));
//        assertThat(((Uncertainty)result).getUncertaintyInterval(), is(new Interval(5, true, 17, true)));

        result = evaluationResult.forExpression("DateTimeDurationBetweenUncertainAdd").value();
        Assert.assertTrue(((Interval)result).getStart().equals(34));
        Assert.assertTrue(((Interval)result).getEnd().equals(88));

        result = evaluationResult.forExpression("DateTimeDurationBetweenUncertainSubtract").value();
        Assert.assertTrue(((Interval)result).getStart().equals(12));
        Assert.assertTrue(((Interval)result).getEnd().equals(28));

        result = evaluationResult.forExpression("DateTimeDurationBetweenUncertainMultiply").value();
        Assert.assertTrue(((Interval)result).getStart().equals(289));
        Assert.assertTrue(((Interval)result).getEnd().equals(1936));

        result = evaluationResult.forExpression("DateTimeDurationBetweenMonthUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeDurationBetweenMonthUncertain2").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeDurationBetweenMonthUncertain3").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeDurationBetweenMonthUncertain4").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeDurationBetweenMonthUncertain5").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeDurationBetweenMonthUncertain6").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeDurationBetweenMonthUncertain7").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DurationInYears").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("TimeDurationBetweenHour").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("TimeDurationBetweenHourDiffPrecision").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("TimeDurationBetweenMinute").value();
        assertThat(result, is(4));

        result = evaluationResult.forExpression("TimeDurationBetweenSecond").value();
        assertThat(result, is(4));

        result = evaluationResult.forExpression("TimeDurationBetweenMillis").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("DurationInHoursA").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DurationInMinutesA").value();
        assertThat(result, is(45));

//        result = evaluationResult.forExpression("DurationInDaysA").value();
//        assertThat(result, is(1));

        result = evaluationResult.forExpression("DurationInHoursAA").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DurationInMinutesAA").value();
        assertThat(result, is(45));

        result = evaluationResult.forExpression("DurationInDaysAA").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DateTimeNow").value();
        assertThat(result, is(true));

        DateTime evaluationDateTime = new DateTime(null, 2016, 6, 10, 5, 5, 4, 999);
        //context = new Context(library, evaluationDateTime.getDateTime().toZonedDateTime());
        result = evaluationResult.forExpression("Issue34A").value();
        //Assert.assertTrue(EquivalentEvaluator.equivalent(result, evaluationDateTime));
        //Assert.assertTrue(((DateTime) result).getDateTime().getOffset().equals(evaluationDateTime.getDateTime().getOffset()));

        result = evaluationResult.forExpression("DateTimeSameAsYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameAsYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameAsMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameAsMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameAsDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameAsDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameAsHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameAsHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameAsMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameAsMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameAsSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameAsSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameAsMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameAsMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameAsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SameAsTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SameAsTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeSameAsHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameAsHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeSameAsMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameAsMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeSameAsSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameAsSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeSameAsMillisTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameAsMillisFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrAfterYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMonthTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMonthTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrAfterDayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrAfterHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrAfterSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMillisecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMillisecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrAfterNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SameOrAfterTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SameOrAfterTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeSameOrAfterHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameOrAfterHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameOrAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeSameOrAfterMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameOrAfterMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameOrAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeSameOrAfterSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameOrAfterSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameOrAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeSameOrAfterMillisTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameOrAfterMillisTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeSameOrAfterMillisFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("OnOrAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("Issue32DateTime").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMonthTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMonthTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeDayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMillisecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMillisecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SameOrBeforeTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SameOrBeforeTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeSubtract5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2000, 10, 10)));

        result = evaluationResult.forExpression("DateTimeSubtract5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 1, 10)));

        result = evaluationResult.forExpression("DateTimeSubtractMonthsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2004, 11, 10)));

        result = evaluationResult.forExpression("DateTimeSubtractThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2018, 5, 2)));

        result = evaluationResult.forExpression("DateTimeSubtractYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2017, 5, 23)));

        result = evaluationResult.forExpression("DateTimeSubtract5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 5)));

        result = evaluationResult.forExpression("DateTimeSubtractDaysUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 5, 30)));

        result = evaluationResult.forExpression("DateTimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5)));

        result = evaluationResult.forExpression("DateTimeSubtractHoursUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 9, 23)));

        result = evaluationResult.forExpression("DateTimeSubtract5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5)));

        result = evaluationResult.forExpression("DateTimeSubtractMinutesUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 4, 59)));

        result = evaluationResult.forExpression("DateTimeSubtract5Seconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5)));

        result = evaluationResult.forExpression("DateTimeSubtract1YearInSeconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2015, 5)));

        result = evaluationResult.forExpression("DateTimeSubtract15HourPrecisionSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 9, 30, 19, 20, 30)));


        result = evaluationResult.forExpression("DateTimeSubtractSecondsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 4, 59)));

        result = evaluationResult.forExpression("DateTimeSubtract5Milliseconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5, 5)));

        result = evaluationResult.forExpression("DateTimeSubtractMillisecondsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 5, 4, 999)));

        result = evaluationResult.forExpression("DateTimeSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012)));

        result = evaluationResult.forExpression("DateTimeSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012)));

        result = evaluationResult.forExpression("DateSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2012)));

        result = evaluationResult.forExpression("DateSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2012)));

        result = evaluationResult.forExpression("DateSubtract33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014,5)));

        result = evaluationResult.forExpression("DateSubtract1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2013,6)));

        result = evaluationResult.forExpression("TimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 59, 59, 999)));

        result = evaluationResult.forExpression("TimeSubtract1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 58, 59, 999)));

        result = evaluationResult.forExpression("TimeSubtract1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 58, 999)));

        result = evaluationResult.forExpression("TimeSubtract1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 58, 999)));

        result = evaluationResult.forExpression("TimeSubtract5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 58, 59, 999)));

        result = evaluationResult.forExpression("TimeSubtract5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 59, 59, 999)));

        result = evaluationResult.forExpression("TimeTest2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeTodayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeTodayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeSameOrBeforeTodayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAddTodayTrue").value();
        assertThat(result, is(true));

//        context = new Context(library, new DateTime(TemporalHelper.getDefaultOffset(), 2016, 6, 10, 5, 5, 4, 999));
//        result = evaluationResult.forExpression("Issue34B").value();
//        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10)));
//        Assert.assertTrue(((DateTime) result).getDateTime().getOffset().equals(TemporalHelper.getDefaultZoneOffset()));


    }
}
