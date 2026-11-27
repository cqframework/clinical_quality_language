package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/**
 * [#1938](https://github.com/cqframework/clinical_quality_language/issues/1938): a DateTime or Time
 * is uncertain through milliseconds, so one specified only to the duration's own precision still
 * gives an uncertainty.
 */
class DurationBetweenEvaluatorTest {
    private val utc = BigDecimal("0")

    private fun dateTime(vararg elements: Int) = DateTime(utc, *elements)

    private fun assertUncertainty(low: Int, high: Int, actual: Value?) {
        val interval = assertIs<Interval>(actual)
        assertEquals(low.toCqlInteger(), interval.low)
        assertEquals(high.toCqlInteger(), interval.high)
        assertTrue(interval.isUncertain)
    }

    @Test
    fun dateTimeAtTheDurationPrecisionIsUncertain() {
        // DateTime(2014, 1, 15) can be as late as 2014-01-15T23:59:59.999
        assertUncertainty(
            16,
            44,
            DurationBetweenEvaluator.duration(
                dateTime(2014, 1, 15),
                dateTime(2014, 2),
                Precision.DAY,
            ),
        )
        assertUncertainty(
            4,
            16,
            DurationBetweenEvaluator.duration(dateTime(2005), dateTime(2006, 5), Precision.MONTH),
        )
        assertUncertainty(
            3,
            4,
            DurationBetweenEvaluator.duration(
                dateTime(2014, 1, 1),
                dateTime(2014, 1, 5),
                Precision.DAY,
            ),
        )
        assertUncertainty(
            1,
            2,
            DurationBetweenEvaluator.duration(
                dateTime(2014, 1, 1),
                dateTime(2014, 1, 15),
                Precision.WEEK,
            ),
        )
    }

    @Test
    fun bothArgumentsUncertain() {
        // Previously null
        assertUncertainty(
            4,
            5,
            DurationBetweenEvaluator.duration(dateTime(2005), dateTime(2010), Precision.YEAR),
        )
        assertUncertainty(
            0,
            23,
            DurationBetweenEvaluator.duration(dateTime(2005), dateTime(2006), Precision.MONTH),
        )
        assertUncertainty(
            1,
            1999,
            DurationBetweenEvaluator.duration(Time(10, 0, 0), Time(10, 0, 1), Precision.MILLISECOND),
        )
    }

    @Test
    fun negativeDuration() {
        assertUncertainty(
            -44,
            -16,
            DurationBetweenEvaluator.duration(
                dateTime(2014, 2),
                dateTime(2014, 1, 15),
                Precision.DAY,
            ),
        )
    }

    @Test
    fun timeAtTheDurationPrecisionIsUncertain() {
        // @T06 is any time from 06:00:00.000 to 06:59:59.999 (cqframework/cql-tests#67)
        assertUncertainty(
            0,
            1,
            DurationBetweenEvaluator.duration(Time(6), Time(7, 0, 0), Precision.HOUR),
        )
    }

    @Test
    fun secondsAndMillisecondsAreNotWidened() {
        assertEquals(
            1.toCqlInteger(),
            DurationBetweenEvaluator.duration(
                dateTime(2017, 1, 1, 0, 0, 0),
                dateTime(2017, 1, 1, 1, 0, 0),
                Precision.HOUR,
            ),
        )
        assertEquals(
            1.toCqlInteger(),
            DurationBetweenEvaluator.duration(
                dateTime(2017, 1, 1, 0, 0, 0, 0),
                dateTime(2017, 1, 1, 1, 0, 0, 0),
                Precision.HOUR,
            ),
        )
        assertEquals(
            5.toCqlInteger(),
            DurationBetweenEvaluator.duration(Time(10, 0, 0), Time(10, 0, 5), Precision.SECOND),
        )
    }

    @Test
    fun dateIsUncertainOnlyBelowTheDurationPrecision() {
        assertEquals(
            4.toCqlInteger(),
            DurationBetweenEvaluator.duration(Date(2014, 1, 1), Date(2014, 1, 5), Precision.DAY),
        )
        assertUncertainty(
            17,
            44,
            DurationBetweenEvaluator.duration(Date(2014, 1, 15), Date(2014, 2), Precision.DAY),
        )
    }

    /**
     * A month that would end on a day the ending month doesn't have ends on its last day, as
     * `Date(2014, 1, 31) + 1 month` is `Date(2014, 2, 28)`.
     */
    @Test
    fun monthsFromTheEndOfAMonth() {
        assertEquals(
            1.toCqlInteger(),
            DurationBetweenEvaluator.duration(Date(2014, 1, 31), Date(2014, 2, 28), Precision.MONTH),
        )
        assertEquals(
            1.toCqlInteger(),
            DurationBetweenEvaluator.duration(
                dateTime(2014, 1, 31, 0, 0, 0, 0),
                dateTime(2014, 2, 28, 0, 0, 0, 0),
                Precision.MONTH,
            ),
        )
        assertEquals(
            4.toCqlInteger(),
            DurationBetweenEvaluator.duration(
                dateTime(2005, 12, 31, 23, 59, 59, 999),
                dateTime(2006, 5, 1, 0, 0, 0, 0),
                Precision.MONTH,
            ),
        )
        // Not yet a whole month: Jan 31 10:00 plus a month is Feb 28 10:00
        assertEquals(
            0.toCqlInteger(),
            DurationBetweenEvaluator.duration(
                dateTime(2014, 1, 31, 10, 0, 0, 0),
                dateTime(2014, 2, 28, 9, 0, 0, 0),
                Precision.MONTH,
            ),
        )
        assertEquals(
            (-1).toCqlInteger(),
            DurationBetweenEvaluator.duration(Date(2014, 3, 31), Date(2014, 2, 28), Precision.MONTH),
        )
    }
}
