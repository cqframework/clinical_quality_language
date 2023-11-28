package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.elm.executing.AfterEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.InvalidDateTime;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings("removal")
public class CqlDateTimeOperatorsTest extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlDateTimeOperatorsTest");

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AddEvaluator#evaluate(Context)}
     */
    @Test
    public void testAdd() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        Object result = engine.expression(library, "DateTimeAdd5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2010, 10, 10)));

        try {
            engine.expression(library, "DateTimeAddInvalidYears").value();
            Assert.fail();
        }
        catch (InvalidDateTime ae) {
            // pass
        }

        result = engine.expression(library, "DateTimeAdd5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 10, 10)));

        result = engine.expression(library, "DateTimeAddMonthsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2006, 3, 10)));

        result = engine.expression(library, "DateTimeAddThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2018, 5, 23)));

        result = engine.expression(library, "DateTimeAddYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2019, 5, 23)));

        result = engine.expression(library, "DateTimeAdd5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 15)));

        result = engine.expression(library, "DateTimeAddDaysOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 7, 1)));

        result = engine.expression(library, "DateTimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 10)));

        result = engine.expression(library, "DateTimeAdd5HoursWithLeftMinPrecisionSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 10, 20, 30)));

        result = engine.expression(library, "DateTimeAdd5HoursWithLeftMinPrecisionDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10)));

        result = engine.expression(library, "DateTimeAdd5HoursWithLeftMinPrecisionDayOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 11)));

        result = engine.expression(library, "DateAdd2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2016)));

        result = engine.expression(library, "DateAdd2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2016)));

        result = engine.expression(library, "DateAdd33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014,7)));

        result = engine.expression(library, "DateAdd1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2015,6)));

        result = engine.expression(library, "DateTimeAddHoursOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 6, 11, 0)));

        result = engine.expression(library, "DateTimeAdd5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 10)));

        result = engine.expression(library, "DateTimeAddMinutesOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 6, 0)));

        result = engine.expression(library, "DateTimeAdd5Seconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5, 10)));

        result = engine.expression(library, "DateTimeAddSecondsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 6, 0)));

        result = engine.expression(library, "DateTimeAdd5Milliseconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5, 5, 10)));

        result = engine.expression(library, "DateTimeAddMillisecondsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 5, 6, 0)));

        result = engine.expression(library, "DateTimeAddLeapYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2013, 2, 28)));

        result = engine.expression(library, "DateTimeAdd2YearsByMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016)));

        result = engine.expression(library, "DateTimeAdd2YearsByDays").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016)));

        result = engine.expression(library, "DateTimeAdd2YearsByDaysRem5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016)));

        result = engine.expression(library, "TimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = engine.expression(library, "TimeAdd1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 59, 999)));

        result = engine.expression(library, "TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 999)));

        result = engine.expression(library, "TimeAdd1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 0)));

        result = engine.expression(library, "TimeAdd5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(21, 0, 59, 999)));

        // checking access ordering and returning correct result
        result = engine.expression(library, "TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 999)));

        result = engine.expression(library, "TimeAdd5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AfterEvaluator#evaluate(Context)}
     */
    @Test
    public void testAfter() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        Object result = engine.expression(library, "DateTimeAfterYearTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeAfterYearFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeAfterMonthTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeAfterMonthFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeAfterDayTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeAfterDayTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeAfterDayFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeAfterHourTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeAfterHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeAfterMinuteTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeAfterSecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeAfterSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeAfterMillisecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeAfterUncertain").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeAfterHourTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeAfterHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeAfterMinuteTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeAfterSecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeAfterSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeAfterMillisecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeAfterTimeCstor").value();
        assertThat(result, is(true));
        try {
            AfterEvaluator.after(12, "This is an error", null, engine.getState());
            Assert.fail();
        }
        catch (CqlException e) {
            // pass
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.BeforeEvaluator#evaluate(Context)}
     */
    @Test
    public void testBefore() {

        Object result = engine.expression(library, "DateTimeBeforeYearTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeBeforeYearFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeBeforeMonthTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeBeforeMonthFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeBeforeDayTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeBeforeDayTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeBeforeDayFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeBeforeHourTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeBeforeHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeBeforeMinuteTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeBeforeSecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeBeforeMillisecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "BeforeTimezoneTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "BeforeTimezoneFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeBeforeHourTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeBeforeHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeBeforeMinuteTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeBeforeSecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeBeforeMillisecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeBeforeMillisecondFalse").value();
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DateTimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testDateTime() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        Object result = engine.expression(library, "DateTimeYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2003)));

        result = engine.expression(library, "DateTimeMonth").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2003, 10)));

        result = engine.expression(library, "DateTimeDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2003, 10, 29)));

        result = engine.expression(library, "DateTimeHour").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2003, 10, 29, 20)));

        result = engine.expression(library, "DateTimeMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2003, 10, 29, 20, 50)));

        result = engine.expression(library, "DateTimeSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2003, 10, 29, 20, 50, 33)));

        result = engine.expression(library, "DateTimeMillisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2003, 10, 29, 20, 50, 33, 955)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DateTimeComponentFromEvaluator#evaluate(Context)}
     */
    @Test
    public void testDateTimeComponentFrom() {

        Object result = engine.expression(library, "DateTimeComponentFromYear").value();
        assertThat(result, is(2003));

        result = engine.expression(library, "DateTimeComponentFromMonth").value();
        assertThat(result, is(10));

        result = engine.expression(library, "DateTimeComponentFromMonthMinBoundary").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DateTimeComponentFromDay").value();
        assertThat(result, is(29));

        result = engine.expression(library, "DateTimeComponentFromHour").value();
        assertThat(result, is(20));

        result = engine.expression(library, "DateTimeComponentFromMinute").value();
        assertThat(result, is(50));

        result = engine.expression(library, "DateTimeComponentFromSecond").value();
        assertThat(result, is(33));

        result = engine.expression(library, "DateTimeComponentFromMillisecond").value();
        assertThat(result, is(955));

        result = engine.expression(library, "DateTimeComponentFromTimezone").value();
        assertThat(result, is(new BigDecimal("1.0")));

        result = engine.expression(library, "DateTimeComponentFromDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2003, 10, 29)));

        result = engine.expression(library, "TimeComponentFromHour").value();
        assertThat(result, is(23));

        result = engine.expression(library, "TimeComponentFromMinute").value();
        assertThat(result, is(20));

        result = engine.expression(library, "TimeComponentFromSecond").value();
        assertThat(result, is(15));

        result = engine.expression(library, "TimeComponentFromMilli").value();
        assertThat(result, is(555));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DifferenceBetweenEvaluator#evaluate(Context)}
     */
    @Test
    public void testDifference() {

        Object result = engine.expression(library, "DateTimeDifferenceYear").value();
        assertThat(result, is(5));

        result = engine.expression(library, "DateTimeDifferenceMonth").value();
        assertThat(result, is(8));

        result = engine.expression(library, "DateTimeDifferenceDay").value();
        assertThat(result, is(10));

        result = engine.expression(library, "DateTimeDifferenceHour").value();
        assertThat(result, is(8));

        result = engine.expression(library, "DateTimeDifferenceMinute").value();
        assertThat(result, is(9));

        result = engine.expression(library, "DateTimeDifferenceSecond").value();
        assertThat(result, is(5));

        result = engine.expression(library, "DateTimeDifferenceMillisecond").value();
        assertThat(result, is(3600400));

        result = engine.expression(library, "DateTimeDifferenceWeeks").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DateTimeDifferenceWeeks2").value();
        assertThat(result, is(2));

        result = engine.expression(library, "DateTimeDifferenceWeeks2").value();
        assertThat(result, is(2));

        result = engine.expression(library, "DateTimeDifferenceNegative").value();
        assertThat(result, is(-18));

        result = engine.expression(library, "DateTimeDifferenceUncertain").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeDifferenceHour").value();
        assertThat(result, is(3));

        result = engine.expression(library, "TimeDifferenceMinute").value();
        assertThat(result, is(5));

        result = engine.expression(library, "TimeDifferenceSecond").value();
        assertThat(result, is(5));

        result = engine.expression(library, "TimeDifferenceMillis").value();
        assertThat(result, is(-5));

        result = engine.expression(library, "DifferenceInHoursA").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DifferenceInMinutesA").value();
        assertThat(result, is(45));

        result = engine.expression(library, "DifferenceInDaysA").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DifferenceInHoursAA").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DifferenceInMinutesAA").value();
        assertThat(result, is(45));

        result = engine.expression(library, "DifferenceInDaysAA").value();
        assertThat(result, is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DurationBetweenEvaluator#evaluate(Context)}
     */
    @Test
    public void testDuration() {

        Object result = engine.expression(library, "DateTimeDurationBetweenYear").value();
        assertThat(result, is(5));

        result = engine.expression(library, "DurationInWeeks").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DurationInWeeks2").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DurationInWeeks3").value();
        assertThat(result, is(2));

        result = engine.expression(library, "DateTimeDurationBetweenYearOffset").value();
        assertThat(result, is(4));

        result = engine.expression(library, "DateTimeDurationBetweenMonth").value();
        assertThat(result, is(0));

        result = engine.expression(library, "DateTimeDurationBetweenDaysDiffYears").value();
        assertThat(result, is(-788));

        result = engine.expression(library, "DateTimeDurationBetweenUncertainInterval").value();
        Assert.assertTrue(((Interval)result).getStart().equals(17));
        Assert.assertTrue(((Interval)result).getEnd().equals(44));

        result = engine.expression(library, "DateTimeDurationBetweenUncertainInterval2").value();
        Assert.assertTrue(((Interval)result).getStart().equals(5));
        Assert.assertTrue(((Interval)result).getEnd().equals(16));
//        assertThat(((Uncertainty)result).getUncertaintyInterval(), is(new Interval(5, true, 17, true)));

        result = engine.expression(library, "DateTimeDurationBetweenUncertainAdd").value();
        Assert.assertTrue(((Interval)result).getStart().equals(34));
        Assert.assertTrue(((Interval)result).getEnd().equals(88));

        result = engine.expression(library, "DateTimeDurationBetweenUncertainSubtract").value();
        Assert.assertTrue(((Interval)result).getStart().equals(12));
        Assert.assertTrue(((Interval)result).getEnd().equals(28));

        result = engine.expression(library, "DateTimeDurationBetweenUncertainMultiply").value();
        Assert.assertTrue(((Interval)result).getStart().equals(289));
        Assert.assertTrue(((Interval)result).getEnd().equals(1936));

        try {
            result = engine.expression(library, "DateTimeDurationBetweenUncertainDiv").value();
            Assert.fail();
        } catch (RuntimeException re) {
            // pass
        }

        result = engine.expression(library, "DateTimeDurationBetweenMonthUncertain").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeDurationBetweenMonthUncertain2").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "DateTimeDurationBetweenMonthUncertain3").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeDurationBetweenMonthUncertain4").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeDurationBetweenMonthUncertain5").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeDurationBetweenMonthUncertain6").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeDurationBetweenMonthUncertain7").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DurationInYears").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TimeDurationBetweenHour").value();
        assertThat(result, is(2));

        result = engine.expression(library, "TimeDurationBetweenHourDiffPrecision").value();
        assertThat(result, is(1));

        result = engine.expression(library, "TimeDurationBetweenMinute").value();
        assertThat(result, is(4));

        result = engine.expression(library, "TimeDurationBetweenSecond").value();
        assertThat(result, is(4));

        result = engine.expression(library, "TimeDurationBetweenMillis").value();
        assertThat(result, is(5));

        result = engine.expression(library, "DurationInHoursA").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DurationInMinutesA").value();
        assertThat(result, is(45));

//        result = engine.expression(library, "DurationInDaysA").value();
//        assertThat(result, is(1));

        result = engine.expression(library, "DurationInHoursAA").value();
        assertThat(result, is(1));

        result = engine.expression(library, "DurationInMinutesAA").value();
        assertThat(result, is(45));

        result = engine.expression(library, "DurationInDaysAA").value();
        assertThat(result, is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.NowEvaluator#evaluate(Context)}
     */
    @Test
    public void testNow() {
        Object result = engine.expression(library, "DateTimeNow").value();
        assertThat(result, is(true));

        DateTime evaluationDateTime = new DateTime(getBigDecimalZoneOffset(), 2016, 6, 10, 5, 5, 4, 999);
        result = engine.expression(library, "Issue34A", evaluationDateTime.getDateTime().toZonedDateTime()).value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, evaluationDateTime));
        Assert.assertTrue(((DateTime) result).getDateTime().getOffset().equals(evaluationDateTime.getDateTime().getOffset()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SameAsEvaluator#evaluate(Context)}
     */
    @Test
    public void testSameAs() {

        Object result = engine.expression(library, "DateTimeSameAsYearTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameAsYearFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameAsMonthTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameAsMonthFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameAsDayTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameAsDayFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameAsHourTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameAsHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameAsMinuteTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameAsMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameAsSecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameAsSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameAsMillisecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameAsMillisecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameAsNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "SameAsTimezoneTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "SameAsTimezoneFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeSameAsHourTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameAsHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeSameAsMinuteTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameAsMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeSameAsSecondTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameAsSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeSameAsMillisTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameAsMillisFalse").value();
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SameOrAfterEvaluator#evaluate(Context)}
     */
    @Test
    public void testSameOrAfter() {
        Object result = engine.expression(library, "DateTimeSameOrAfterYearTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterYearTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterYearFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrAfterMonthTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterMonthTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterMonthFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrAfterDayTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterDayTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterDayFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrAfterHourTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterHourTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrAfterMinuteTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterMinuteTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrAfterSecondTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterSecondTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrAfterMillisecondTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterMillisecondTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrAfterNull1").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "SameOrAfterTimezoneTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "SameOrAfterTimezoneFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeSameOrAfterHourTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameOrAfterHourTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameOrAfterHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeSameOrAfterMinuteTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameOrAfterMinuteTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameOrAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeSameOrAfterSecondTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameOrAfterSecondTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameOrAfterSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "TimeSameOrAfterMillisTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameOrAfterMillisTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "TimeSameOrAfterMillisFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "OnOrAfterTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "Issue32DateTime").value();
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SameOrBeforeEvaluator#evaluate(Context)}
     */
    @Test
    public void testSameOrBefore() {
        Object result = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeMonthTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeMonthTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeMonthFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeDayTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeDayTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeDayFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeHourTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeHourTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeHourFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeMinuteTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeMinuteTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeSecondTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeSecondTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeMillisecondTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeMillisecondTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeNull1").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "SameOrBeforeTimezoneTrue").value();
        assertThat(result, is(true));

        result = engine.expression(library, "SameOrBeforeTimezoneFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SubtractEvaluator#evaluate(Context)}
     */
    @Test
    public void testSubtract() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        Object result = engine.expression(library, "DateTimeSubtract5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2000, 10, 10)));

        try {
            engine.expression(library, "DateTimeSubtractInvalidYears").value();
            Assert.fail();
        } catch (InvalidDateTime ae) {
            // pass
        }

        result = engine.expression(library, "DateTimeSubtract5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 1, 10)));

        result = engine.expression(library, "DateTimeSubtractMonthsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2004, 11, 10)));

        result = engine.expression(library, "DateTimeSubtractThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2018, 5, 2)));

        result = engine.expression(library, "DateTimeSubtractYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2017, 5, 23)));

        result = engine.expression(library, "DateTimeSubtract5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 5)));

        result = engine.expression(library, "DateTimeSubtractDaysUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 5, 30)));

        result = engine.expression(library, "DateTimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5)));

        result = engine.expression(library, "DateTimeSubtractHoursUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 6, 9, 23)));

        result = engine.expression(library, "DateTimeSubtract5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5)));

        result = engine.expression(library, "DateTimeSubtractMinutesUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 4, 59)));

        result = engine.expression(library, "DateTimeSubtract5Seconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5, 5)));

        result = engine.expression(library, "DateTimeSubtract1YearInSeconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2015, 5)));

        result = engine.expression(library, "DateTimeSubtract15HourPrecisionSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 9, 30, 19, 20, 30)));


        result = engine.expression(library, "DateTimeSubtractSecondsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 4, 59)));

        result = engine.expression(library, "DateTimeSubtract5Milliseconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5, 5, 5)));

        result = engine.expression(library, "DateTimeSubtractMillisecondsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 5, 4, 999)));

        result = engine.expression(library, "DateTimeSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012)));

        result = engine.expression(library, "DateTimeSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012)));

        result = engine.expression(library, "DateSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2012)));

        result = engine.expression(library, "DateSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2012)));

        result = engine.expression(library, "DateSubtract33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014,5)));

        result = engine.expression(library, "DateSubtract1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2013,6)));

        result = engine.expression(library, "TimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 59, 59, 999)));

        result = engine.expression(library, "TimeSubtract1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 58, 59, 999)));

        result = engine.expression(library, "TimeSubtract1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 58, 999)));

        result = engine.expression(library, "TimeSubtract1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 58, 999)));

        result = engine.expression(library, "TimeSubtract5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 58, 59, 999)));

        result = engine.expression(library, "TimeSubtract5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testTime() {

        Object result = engine.expression(library, "TimeTest2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TimeOfDayEvaluator#evaluate(Context)}
     */
    @Test
    public void testTimeOfDay() {
        // TODO: uncomment once Time(x,x,x,x,x) format is fixed
        //Context context = new Context(library);
        // Object result = engine.expression(library, "TimeOfDayTest").value();
        // assertThat(((Time)result).getPartial().getValue(0), is(10));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TodayEvaluator#evaluate(Context)}
     */
    @Test
    public void testToday() {

        Object result = engine.expression(library, "DateTimeSameOrBeforeTodayTrue1").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeTodayTrue2").value();
        assertThat(result, is(true));

        result = engine.expression(library, "DateTimeSameOrBeforeTodayFalse").value();
        assertThat(result, is(false));

        result = engine.expression(library, "DateTimeAddTodayTrue").value();
        assertThat(result, is(true));

//        context = new Context(library, new DateTime(TemporalHelper.getDefaultOffset(), 2016, 6, 10, 5, 5, 4, 999));
//        result = engine.expression(library, "Issue34B").value();
//        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10)));
//        Assert.assertTrue(((DateTime) result).getDateTime().getOffset().equals(TemporalHelper.getDefaultZoneOffset()));
    }
}
