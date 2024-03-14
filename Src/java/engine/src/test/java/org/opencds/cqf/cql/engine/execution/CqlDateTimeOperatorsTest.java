package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.elm.executing.AfterEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.InvalidDateTime;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("removal")
public class CqlDateTimeOperatorsTest extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlDateTimeOperatorsTest");

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AddEvaluator#evaluate(Context)}
     */
    @Test
    public void testAdd() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var value = engine.expression(library, "DateTimeAdd5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2010, 10, 10)));

        try {
            engine.expression(library, "DateTimeAddInvalidYears").value();
            Assert.fail();
        } catch (InvalidDateTime ae) {
            // pass
        }

        value = engine.expression(library, "DateTimeAdd5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 10, 10)));

        value = engine.expression(library, "DateTimeAddMonthsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2006, 3, 10)));

        value = engine.expression(library, "DateTimeAddThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2018, 5, 23)));

        value = engine.expression(library, "DateTimeAddYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2019, 5, 23)));

        value = engine.expression(library, "DateTimeAdd5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 15)));

        value = engine.expression(library, "DateTimeAddDaysOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 7, 1)));

        value = engine.expression(library, "DateTimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 10)));

        value = engine.expression(library, "DateTimeAdd5HoursWithLeftMinPrecisionSecond")
                .value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 10, 20, 30)));

        value = engine.expression(library, "DateTimeAdd5HoursWithLeftMinPrecisionDay")
                .value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10)));

        value = engine.expression(library, "DateTimeAdd5HoursWithLeftMinPrecisionDayOverflow")
                .value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 11)));

        value = engine.expression(library, "DateAdd2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2016)));

        value = engine.expression(library, "DateAdd2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2016)));

        value = engine.expression(library, "DateAdd33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2014, 7)));

        value = engine.expression(library, "DateAdd1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2015, 6)));

        value = engine.expression(library, "DateTimeAddHoursOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 6, 11, 0)));

        value = engine.expression(library, "DateTimeAdd5Minutes").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 10)));

        value = engine.expression(library, "DateTimeAddMinutesOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 6, 0)));

        value = engine.expression(library, "DateTimeAdd5Seconds").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5, 10)));

        value = engine.expression(library, "DateTimeAddSecondsOverflow").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 6, 0)));

        value = engine.expression(library, "DateTimeAdd5Milliseconds").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5, 5, 10)));

        value = engine.expression(library, "DateTimeAddMillisecondsOverflow").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 5, 6, 0)));

        value = engine.expression(library, "DateTimeAddLeapYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2013, 2, 28)));

        value = engine.expression(library, "DateTimeAdd2YearsByMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016)));

        value = engine.expression(library, "DateTimeAdd2YearsByDays").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016)));

        value = engine.expression(library, "DateTimeAdd2YearsByDaysRem5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016)));

        value = engine.expression(library, "TimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(20, 59, 59, 999)));

        value = engine.expression(library, "TimeAdd1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(16, 0, 59, 999)));

        value = engine.expression(library, "TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(16, 0, 0, 999)));

        value = engine.expression(library, "TimeAdd1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(16, 0, 0, 0)));

        value = engine.expression(library, "TimeAdd5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(21, 0, 59, 999)));

        // checking access ordering and returning correct result
        value = engine.expression(library, "TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(16, 0, 0, 999)));

        value = engine.expression(library, "TimeAdd5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(20, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AfterEvaluator#evaluate(Context)}
     */
    @Test
    public void testAfter() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var value = engine.expression(library, "DateTimeAfterYearTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeAfterYearFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeAfterMonthTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeAfterMonthFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeAfterDayTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeAfterDayTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeAfterDayFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeAfterHourTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeAfterHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeAfterMinuteTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeAfterMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeAfterSecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeAfterSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeAfterMillisecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeAfterMillisecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeAfterUncertain").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeAfterHourTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeAfterHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeAfterMinuteTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeAfterMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeAfterSecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeAfterSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeAfterMillisecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeAfterMillisecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeAfterTimeCstor").value();
        assertThat(value, is(true));
        try {
            AfterEvaluator.after(12, "This is an error", null, engine.getState());
            Assert.fail();
        } catch (CqlException e) {
            // pass
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.BeforeEvaluator#evaluate(Context)}
     */
    @Test
    public void testBefore() {

        var value = engine.expression(library, "DateTimeBeforeYearTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeBeforeYearFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeBeforeMonthTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeBeforeMonthFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeBeforeDayTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeBeforeDayTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeBeforeDayFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeBeforeHourTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeBeforeHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeBeforeMinuteTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeBeforeMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeBeforeSecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeBeforeSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeBeforeMillisecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeBeforeMillisecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "BeforeTimezoneTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "BeforeTimezoneFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeBeforeHourTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeBeforeHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeBeforeMinuteTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeBeforeMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeBeforeSecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeBeforeSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeBeforeMillisecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeBeforeMillisecondFalse").value();
        assertThat(value, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DateTimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testDateTime() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var value = engine.expression(library, "DateTimeYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2003)));

        value = engine.expression(library, "DateTimeMonth").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2003, 10)));

        value = engine.expression(library, "DateTimeDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2003, 10, 29)));

        value = engine.expression(library, "DateTimeHour").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2003, 10, 29, 20)));

        value = engine.expression(library, "DateTimeMinute").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2003, 10, 29, 20, 50)));

        value = engine.expression(library, "DateTimeSecond").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2003, 10, 29, 20, 50, 33)));

        value = engine.expression(library, "DateTimeMillisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(
                value, new DateTime(bigDecimalZoneOffset, 2003, 10, 29, 20, 50, 33, 955)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DateTimeComponentFromEvaluator#evaluate(Context)}
     */
    @Test
    public void testDateTimeComponentFrom() {

        var value = engine.expression(library, "DateTimeComponentFromYear").value();
        assertThat(value, is(2003));

        value = engine.expression(library, "DateTimeComponentFromMonth").value();
        assertThat(value, is(10));

        value = engine.expression(library, "DateTimeComponentFromMonthMinBoundary")
                .value();
        assertThat(value, is(1));

        value = engine.expression(library, "DateTimeComponentFromDay").value();
        assertThat(value, is(29));

        value = engine.expression(library, "DateTimeComponentFromHour").value();
        assertThat(value, is(20));

        value = engine.expression(library, "DateTimeComponentFromMinute").value();
        assertThat(value, is(50));

        value = engine.expression(library, "DateTimeComponentFromSecond").value();
        assertThat(value, is(33));

        value = engine.expression(library, "DateTimeComponentFromMillisecond").value();
        assertThat(value, is(955));

        value = engine.expression(library, "DateTimeComponentFromTimezone").value();
        assertThat(value, is(new BigDecimal("1.0")));

        value = engine.expression(library, "DateTimeComponentFromDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2003, 10, 29)));

        value = engine.expression(library, "TimeComponentFromHour").value();
        assertThat(value, is(23));

        value = engine.expression(library, "TimeComponentFromMinute").value();
        assertThat(value, is(20));

        value = engine.expression(library, "TimeComponentFromSecond").value();
        assertThat(value, is(15));

        value = engine.expression(library, "TimeComponentFromMilli").value();
        assertThat(value, is(555));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DifferenceBetweenEvaluator#evaluate(Context)}
     */
    @Test
    public void testDifference() {

        var value = engine.expression(library, "DateTimeDifferenceYear").value();
        assertThat(value, is(5));

        value = engine.expression(library, "DateTimeDifferenceMonth").value();
        assertThat(value, is(8));

        value = engine.expression(library, "DateTimeDifferenceDay").value();
        assertThat(value, is(10));

        value = engine.expression(library, "DateTimeDifferenceHour").value();
        assertThat(value, is(8));

        value = engine.expression(library, "DateTimeDifferenceMinute").value();
        assertThat(value, is(9));

        value = engine.expression(library, "DateTimeDifferenceSecond").value();
        assertThat(value, is(5));

        value = engine.expression(library, "DateTimeDifferenceMillisecond").value();
        assertThat(value, is(3600400));

        value = engine.expression(library, "DateTimeDifferenceWeeks").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DateTimeDifferenceWeeks2").value();
        assertThat(value, is(2));

        value = engine.expression(library, "DateTimeDifferenceWeeks2").value();
        assertThat(value, is(2));

        value = engine.expression(library, "DateTimeDifferenceNegative").value();
        assertThat(value, is(-18));

        value = engine.expression(library, "DateTimeDifferenceUncertain").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeDifferenceHour").value();
        assertThat(value, is(3));

        value = engine.expression(library, "TimeDifferenceMinute").value();
        assertThat(value, is(5));

        value = engine.expression(library, "TimeDifferenceSecond").value();
        assertThat(value, is(5));

        value = engine.expression(library, "TimeDifferenceMillis").value();
        assertThat(value, is(-5));

        value = engine.expression(library, "DifferenceInHoursA").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DifferenceInMinutesA").value();
        assertThat(value, is(45));

        value = engine.expression(library, "DifferenceInDaysA").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DifferenceInHoursAA").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DifferenceInMinutesAA").value();
        assertThat(value, is(45));

        value = engine.expression(library, "DifferenceInDaysAA").value();
        assertThat(value, is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DurationBetweenEvaluator#evaluate(Context)}
     */
    @Test
    public void testDuration() {

        var value = engine.expression(library, "DateTimeDurationBetweenYear").value();
        assertThat(value, is(5));

        value = engine.expression(library, "DurationInWeeks").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DurationInWeeks2").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DurationInWeeks3").value();
        assertThat(value, is(2));

        value = engine.expression(library, "DateTimeDurationBetweenYearOffset").value();
        assertThat(value, is(4));

        value = engine.expression(library, "DateTimeDurationBetweenMonth").value();
        assertThat(value, is(0));

        value = engine.expression(library, "DateTimeDurationBetweenDaysDiffYears")
                .value();
        assertThat(value, is(-788));

        value = engine.expression(library, "DateTimeDurationBetweenUncertainInterval")
                .value();
        Assert.assertTrue(((Interval) value).getStart().equals(17));
        Assert.assertTrue(((Interval) value).getEnd().equals(44));

        value = engine.expression(library, "DateTimeDurationBetweenUncertainInterval2")
                .value();
        Assert.assertTrue(((Interval) value).getStart().equals(5));
        Assert.assertTrue(((Interval) value).getEnd().equals(16));
        //        assertThat(((Uncertainty)result).getUncertaintyInterval(), is(new Interval(5, true, 17, true)));

        value = engine.expression(library, "DateTimeDurationBetweenUncertainAdd")
                .value();
        Assert.assertTrue(((Interval) value).getStart().equals(34));
        Assert.assertTrue(((Interval) value).getEnd().equals(88));

        value = engine.expression(library, "DateTimeDurationBetweenUncertainSubtract")
                .value();
        Assert.assertTrue(((Interval) value).getStart().equals(12));
        Assert.assertTrue(((Interval) value).getEnd().equals(28));

        value = engine.expression(library, "DateTimeDurationBetweenUncertainMultiply")
                .value();
        Assert.assertTrue(((Interval) value).getStart().equals(289));
        Assert.assertTrue(((Interval) value).getEnd().equals(1936));

        try {
            value = engine.expression(library, "DateTimeDurationBetweenUncertainDiv")
                    .value();
            Assert.fail();
        } catch (RuntimeException re) {
            // pass
        }

        value = engine.expression(library, "DateTimeDurationBetweenMonthUncertain")
                .value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeDurationBetweenMonthUncertain2")
                .value();
        assertThat(value, is(nullValue()));

        value = engine.expression(library, "DateTimeDurationBetweenMonthUncertain3")
                .value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeDurationBetweenMonthUncertain4")
                .value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeDurationBetweenMonthUncertain5")
                .value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeDurationBetweenMonthUncertain6")
                .value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeDurationBetweenMonthUncertain7")
                .value();
        assertThat(value, is(true));

        value = engine.expression(library, "DurationInYears").value();
        assertThat(value, is(1));

        value = engine.expression(library, "TimeDurationBetweenHour").value();
        assertThat(value, is(2));

        value = engine.expression(library, "TimeDurationBetweenHourDiffPrecision")
                .value();
        assertThat(value, is(1));

        value = engine.expression(library, "TimeDurationBetweenMinute").value();
        assertThat(value, is(4));

        value = engine.expression(library, "TimeDurationBetweenSecond").value();
        assertThat(value, is(4));

        value = engine.expression(library, "TimeDurationBetweenMillis").value();
        assertThat(value, is(5));

        value = engine.expression(library, "DurationInHoursA").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DurationInMinutesA").value();
        assertThat(value, is(45));

        //        value = engine.expression(library, "DurationInDaysA").value();
        //        assertThat(value, is(1));

        value = engine.expression(library, "DurationInHoursAA").value();
        assertThat(value, is(1));

        value = engine.expression(library, "DurationInMinutesAA").value();
        assertThat(value, is(45));

        value = engine.expression(library, "DurationInDaysAA").value();
        assertThat(value, is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.NowEvaluator#evaluate(Context)}
     */
    @Test
    public void testNow() {
        var value = engine.expression(library, "DateTimeNow").value();
        assertThat(value, is(true));

        DateTime evaluationDateTime = new DateTime(getBigDecimalZoneOffset(), 2016, 6, 10, 5, 5, 4, 999);
        value = engine.expression(
                        library, "Issue34A", evaluationDateTime.getDateTime().toZonedDateTime())
                .value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, evaluationDateTime));
        Assert.assertTrue(((DateTime) value)
                .getDateTime()
                .getOffset()
                .equals(evaluationDateTime.getDateTime().getOffset()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SameAsEvaluator#evaluate(Context)}
     */
    @Test
    public void testSameAs() {

        var value = engine.expression(library, "DateTimeSameAsYearTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameAsYearFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameAsMonthTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameAsMonthFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameAsDayTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameAsDayFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameAsHourTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameAsHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameAsMinuteTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameAsMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameAsSecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameAsSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameAsMillisecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameAsMillisecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameAsNull").value();
        assertThat(value, is(nullValue()));

        value = engine.expression(library, "SameAsTimezoneTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "SameAsTimezoneFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeSameAsHourTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameAsHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeSameAsMinuteTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameAsMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeSameAsSecondTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameAsSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeSameAsMillisTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameAsMillisFalse").value();
        assertThat(value, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SameOrAfterEvaluator#evaluate(Context)}
     */
    @Test
    public void testSameOrAfter() {
        var value = engine.expression(library, "DateTimeSameOrAfterYearTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterYearTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterYearFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrAfterMonthTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterMonthTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterMonthFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrAfterDayTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterDayTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterDayFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrAfterHourTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterHourTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrAfterMinuteTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterMinuteTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrAfterSecondTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterSecondTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrAfterMillisecondTrue1")
                .value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterMillisecondTrue2")
                .value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrAfterMillisecondFalse")
                .value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrAfterNull1").value();
        assertThat(value, is(nullValue()));

        value = engine.expression(library, "SameOrAfterTimezoneTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "SameOrAfterTimezoneFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeSameOrAfterHourTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameOrAfterHourTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameOrAfterHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeSameOrAfterMinuteTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameOrAfterMinuteTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameOrAfterMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeSameOrAfterSecondTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameOrAfterSecondTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameOrAfterSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "TimeSameOrAfterMillisTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameOrAfterMillisTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "TimeSameOrAfterMillisFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "OnOrAfterTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "Issue32DateTime").value();
        assertThat(value, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SameOrBeforeEvaluator#evaluate(Context)}
     */
    @Test
    public void testSameOrBefore() {
        var value = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeMonthTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeMonthTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeMonthFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeDayTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeDayTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeDayFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeHourTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeHourTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeHourFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeMinuteTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeMinuteTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeMinuteFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeSecondTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeSecondTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeSecondFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeMillisecondTrue1")
                .value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeMillisecondTrue2")
                .value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeMillisecondFalse")
                .value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeNull1").value();
        assertThat(value, is(nullValue()));

        value = engine.expression(library, "SameOrBeforeTimezoneTrue").value();
        assertThat(value, is(true));

        value = engine.expression(library, "SameOrBeforeTimezoneFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeYearFalse").value();
        assertThat(value, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SubtractEvaluator#evaluate(Context)}
     */
    @Test
    public void testSubtract() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var value = engine.expression(library, "DateTimeSubtract5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2000, 10, 10)));

        try {
            engine.expression(library, "DateTimeSubtractInvalidYears").value();
            Assert.fail();
        } catch (InvalidDateTime ae) {
            // pass
        }

        value = engine.expression(library, "DateTimeSubtract5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 1, 10)));

        value = engine.expression(library, "DateTimeSubtractMonthsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2004, 11, 10)));

        value = engine.expression(library, "DateTimeSubtractThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2018, 5, 2)));

        value = engine.expression(library, "DateTimeSubtractYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2017, 5, 23)));

        value = engine.expression(library, "DateTimeSubtract5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 5)));

        value = engine.expression(library, "DateTimeSubtractDaysUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 5, 30)));

        value = engine.expression(library, "DateTimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5)));

        value = engine.expression(library, "DateTimeSubtractHoursUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 6, 9, 23)));

        value = engine.expression(library, "DateTimeSubtract5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5)));

        value = engine.expression(library, "DateTimeSubtractMinutesUnderflow").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 4, 59)));

        value = engine.expression(library, "DateTimeSubtract5Seconds").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5, 5)));

        value = engine.expression(library, "DateTimeSubtract1YearInSeconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2015, 5)));

        value = engine.expression(library, "DateTimeSubtract15HourPrecisionSecond")
                .value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 9, 30, 19, 20, 30)));

        value = engine.expression(library, "DateTimeSubtractSecondsUnderflow").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 4, 59)));

        value = engine.expression(library, "DateTimeSubtract5Milliseconds").value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2005, 5, 10, 5, 5, 5, 5)));

        value = engine.expression(library, "DateTimeSubtractMillisecondsUnderflow")
                .value();
        Assert.assertTrue(
                EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 6, 10, 5, 5, 4, 999)));

        value = engine.expression(library, "DateTimeSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012)));

        value = engine.expression(library, "DateTimeSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012)));

        value = engine.expression(library, "DateSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2012)));

        value = engine.expression(library, "DateSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2012)));

        value = engine.expression(library, "DateSubtract33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2014, 5)));

        value = engine.expression(library, "DateSubtract1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Date(2013, 6)));

        value = engine.expression(library, "TimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(10, 59, 59, 999)));

        value = engine.expression(library, "TimeSubtract1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(15, 58, 59, 999)));

        value = engine.expression(library, "TimeSubtract1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(15, 59, 58, 999)));

        value = engine.expression(library, "TimeSubtract1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(15, 59, 58, 999)));

        value = engine.expression(library, "TimeSubtract5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(10, 58, 59, 999)));

        value = engine.expression(library, "TimeSubtract5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(10, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testTime() {

        var value = engine.expression(library, "TimeTest2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(23, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TimeOfDayEvaluator#evaluate(Context)}
     */
    @Test
    public void testTimeOfDay() {
        // TODO: uncomment once Time(x,x,x,x,x) format is fixed
        // Context context = new Context(library);
        // var value = engine.expression(library, "TimeOfDayTest").value();
        // assertThat(((Time)result).getPartial().getValue(0), is(10));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TodayEvaluator#evaluate(Context)}
     */
    @Test
    public void testToday() {

        var value = engine.expression(library, "DateTimeSameOrBeforeTodayTrue1").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeTodayTrue2").value();
        assertThat(value, is(true));

        value = engine.expression(library, "DateTimeSameOrBeforeTodayFalse").value();
        assertThat(value, is(false));

        value = engine.expression(library, "DateTimeAddTodayTrue").value();
        assertThat(value, is(true));

        //        context = new Context(library, new DateTime(TemporalHelper.getDefaultOffset(), 2016, 6, 10, 5, 5, 4,
        // 999));
        //        value = engine.expression(library, "Issue34B").value();
        //        Assert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(null, 2016, 6, 10)));
        //        Assert.assertTrue(((DateTime)
        // result).getDateTime().getOffset().equals(TemporalHelper.getDefaultZoneOffset()));
    }
}
