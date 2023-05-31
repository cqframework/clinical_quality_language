package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.visiting.EquivalentEvaluator;
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
        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlDateTimeOperatorsTest"), ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()));

        Object result = evaluationResult.expressionResults.get("DateTimeAdd5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2010, 10, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 10, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddMonthsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2006, 3, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2018, 5, 23)));

        result = evaluationResult.expressionResults.get("DateTimeAddYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2019, 5, 23)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 15)));

        result = evaluationResult.expressionResults.get("DateTimeAddDaysOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 7, 1)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5HoursWithLeftMinPrecisionSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 10, 20, 30)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5HoursWithLeftMinPrecisionDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5HoursWithLeftMinPrecisionDayOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 11)));

        result = evaluationResult.expressionResults.get("DateAdd2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2016)));

        result = evaluationResult.expressionResults.get("DateAdd2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2016)));

        result = evaluationResult.expressionResults.get("DateAdd33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014,7)));

        result = evaluationResult.expressionResults.get("DateAdd1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2015,6)));

        result = evaluationResult.expressionResults.get("DateTimeAddHoursOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 11, 0)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddMinutesOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 6, 0)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Seconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddSecondsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 6, 0)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Milliseconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddMillisecondsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 5, 6, 0)));

        result = evaluationResult.expressionResults.get("DateTimeAddLeapYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2013, 2, 28)));

        result = evaluationResult.expressionResults.get("DateTimeAdd2YearsByMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.expressionResults.get("DateTimeAdd2YearsByDays").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.expressionResults.get("DateTimeAdd2YearsByDaysRem5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.expressionResults.get("TimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeAdd1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 999)));

        result = evaluationResult.expressionResults.get("TimeAdd1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("TimeAdd5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(21, 0, 59, 999)));

        // checking access ordering and returning correct result
        result = evaluationResult.expressionResults.get("TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 999)));

        result = evaluationResult.expressionResults.get("TimeAdd5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("DateTimeAfterYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeAfterMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeAfterSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeAfterMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeAfterTimeCstor").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("BeforeTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("BeforeTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003)));

        result = evaluationResult.expressionResults.get("DateTimeMonth").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10)));

        result = evaluationResult.expressionResults.get("DateTimeDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29)));

        result = evaluationResult.expressionResults.get("DateTimeHour").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20)));

        result = evaluationResult.expressionResults.get("DateTimeMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50)));

        result = evaluationResult.expressionResults.get("DateTimeSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50, 33)));

        result = evaluationResult.expressionResults.get("DateTimeMillisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50, 33, 955)));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromYear").value();
        assertThat(result, is(2003));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromMonth").value();
        assertThat(result, is(10));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromMonthMinBoundary").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromDay").value();
        assertThat(result, is(29));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromHour").value();
        assertThat(result, is(20));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromMinute").value();
        assertThat(result, is(50));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromSecond").value();
        assertThat(result, is(33));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromMillisecond").value();
        assertThat(result, is(955));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromTimezone").value();
        assertThat(result, is(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2003, 10, 29)));

        result = evaluationResult.expressionResults.get("TimeComponentFromHour").value();
        assertThat(result, is(23));

        result = evaluationResult.expressionResults.get("TimeComponentFromMinute").value();
        assertThat(result, is(20));

        result = evaluationResult.expressionResults.get("TimeComponentFromSecond").value();
        assertThat(result, is(15));

        result = evaluationResult.expressionResults.get("TimeComponentFromMilli").value();
        assertThat(result, is(555));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceYear").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceMonth").value();
        assertThat(result, is(8));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceDay").value();
        assertThat(result, is(10));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceHour").value();
        assertThat(result, is(8));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceMinute").value();
        assertThat(result, is(9));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceSecond").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceMillisecond").value();
        assertThat(result, is(3600400));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceWeeks").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceWeeks2").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceWeeks2").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceNegative").value();
        assertThat(result, is(-18));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeDifferenceHour").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("TimeDifferenceMinute").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("TimeDifferenceSecond").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("TimeDifferenceMillis").value();
        assertThat(result, is(-5));

        result = evaluationResult.expressionResults.get("DifferenceInHoursA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DifferenceInMinutesA").value();
        assertThat(result, is(45));

        result = evaluationResult.expressionResults.get("DifferenceInDaysA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DifferenceInHoursAA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DifferenceInMinutesAA").value();
        assertThat(result, is(45));

        result = evaluationResult.expressionResults.get("DifferenceInDaysAA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenYear").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("DurationInWeeks").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInWeeks2").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInWeeks3").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenYearOffset").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonth").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenDaysDiffYears").value();
        assertThat(result, is(-788));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainInterval").value();
        Assert.assertTrue(((Interval)result).getStart().equals(17));
        Assert.assertTrue(((Interval)result).getEnd().equals(44));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainInterval2").value();
        Assert.assertTrue(((Interval)result).getStart().equals(5));
        Assert.assertTrue(((Interval)result).getEnd().equals(16));
//        assertThat(((Uncertainty)result).getUncertaintyInterval(), is(new Interval(5, true, 17, true)));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainAdd").value();
        Assert.assertTrue(((Interval)result).getStart().equals(34));
        Assert.assertTrue(((Interval)result).getEnd().equals(88));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainSubtract").value();
        Assert.assertTrue(((Interval)result).getStart().equals(12));
        Assert.assertTrue(((Interval)result).getEnd().equals(28));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainMultiply").value();
        Assert.assertTrue(((Interval)result).getStart().equals(289));
        Assert.assertTrue(((Interval)result).getEnd().equals(1936));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain2").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain3").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain4").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain5").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain6").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain7").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DurationInYears").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenHour").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenHourDiffPrecision").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenMinute").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenSecond").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenMillis").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("DurationInHoursA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInMinutesA").value();
        assertThat(result, is(45));

//        result = evaluationResult.expressionResults.get("DurationInDaysA").value();
//        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInHoursAA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInMinutesAA").value();
        assertThat(result, is(45));

        result = evaluationResult.expressionResults.get("DurationInDaysAA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DateTimeNow").value();
        assertThat(result, is(true));

        DateTime evaluationDateTime = new DateTime(null, 2016, 6, 10, 5, 5, 4, 999);
        //context = new Context(library, evaluationDateTime.getDateTime().toZonedDateTime());
        result = evaluationResult.expressionResults.get("Issue34A").value();
        //Assert.assertTrue(EquivalentEvaluator.equivalent(result, evaluationDateTime));
        //Assert.assertTrue(((DateTime) result).getDateTime().getOffset().equals(evaluationDateTime.getDateTime().getOffset()));

        result = evaluationResult.expressionResults.get("DateTimeSameAsYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SameAsTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SameAsTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameAsHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameAsHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameAsMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameAsMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameAsSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameAsSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameAsMillisTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameAsMillisFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMonthTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMonthTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterDayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMillisecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMillisecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SameOrAfterTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SameOrAfterTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMillisTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMillisTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMillisFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("OnOrAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("Issue32DateTime").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMonthTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMonthTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeDayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMillisecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMillisecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SameOrBeforeTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SameOrBeforeTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2000, 10, 10)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 1, 10)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractMonthsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2004, 11, 10)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2018, 5, 2)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2017, 5, 23)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractDaysUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 5, 30)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractHoursUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 9, 23)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractMinutesUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 4, 59)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Seconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract1YearInSeconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2015, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract15HourPrecisionSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 9, 30, 19, 20, 30)));


        result = evaluationResult.expressionResults.get("DateTimeSubtractSecondsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 4, 59)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Milliseconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractMillisecondsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 5, 4, 999)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012)));

        result = evaluationResult.expressionResults.get("DateSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2012)));

        result = evaluationResult.expressionResults.get("DateSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2012)));

        result = evaluationResult.expressionResults.get("DateSubtract33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014,5)));

        result = evaluationResult.expressionResults.get("DateSubtract1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2013,6)));

        result = evaluationResult.expressionResults.get("TimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 58, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 58, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 58, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 58, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeTest2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeTodayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeTodayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeTodayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAddTodayTrue").value();
        assertThat(result, is(true));

//        context = new Context(library, new DateTime(TemporalHelper.getDefaultOffset(), 2016, 6, 10, 5, 5, 4, 999));
//        result = evaluationResult.expressionResults.get("Issue34B").value();
//        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10)));
//        Assert.assertTrue(((DateTime) result).getDateTime().getOffset().equals(TemporalHelper.getDefaultZoneOffset()));


    }
}
