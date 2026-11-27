package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Quantity

/**
 * Equal and Equivalent on calendar duration quantities (Equal, Equivalent): "calendar-time duration
 * unit conversions shall be performed according to calendar duration semantics".
 */
class CalendarDurationComparisonTest {
    private fun q(value: String, unit: String) =
        Quantity().withValue(BigDecimal(value)).withUnit(unit)

    private fun assertEqual(expected: Boolean?, left: Quantity, right: Quantity) {
        assertEquals(expected, EqualEvaluator.equal(left, right, null), "$left = $right")
        assertEquals(expected, EqualEvaluator.equal(right, left, null), "$right = $left")
    }

    private fun assertEquivalent(expected: Boolean, left: Quantity, right: Quantity) {
        assertEquals(expected, EquivalentEvaluator.equivalent(left, right, null), "$left ~ $right")
        assertEquals(expected, EquivalentEvaluator.equivalent(right, left, null), "$right ~ $left")
    }

    @Test
    fun definiteDurationsEqual() {
        // The spec's list: 1 week = 7 days, 1 day = 24 hours, … 1 second = 1000 milliseconds.
        assertEqual(Boolean.TRUE, q("1", "week"), q("7", "days"))
        assertEqual(Boolean.TRUE, q("1", "day"), q("24", "hours"))
        assertEqual(Boolean.TRUE, q("1", "hour"), q("60", "minutes"))
        assertEqual(Boolean.TRUE, q("1", "minute"), q("60", "seconds"))
        assertEqual(Boolean.TRUE, q("1", "second"), q("1000", "milliseconds"))
        assertEqual(Boolean.TRUE, q("2", "weeks"), q("336", "hours"))
        assertEqual(Boolean.TRUE, q("1.5", "hours"), q("90", "minutes"))
        assertEqual(Boolean.FALSE, q("1", "week"), q("6", "days"))
    }

    @Test
    fun definiteDurationsEqualTheirUcumCounterparts() {
        assertEqual(Boolean.TRUE, q("7", "days"), q("1", "wk"))
        assertEqual(Boolean.TRUE, q("1", "day"), q("24", "h"))
        assertEqual(Boolean.TRUE, q("1", "second"), q("1000", "ms"))
    }

    @Test
    fun yearsAndMonthsEqual() {
        // 1 year = 12 months.
        assertEqual(Boolean.TRUE, q("1", "year"), q("12", "months"))
        assertEqual(Boolean.TRUE, q("2", "years"), q("24", "month"))
        assertEqual(Boolean.FALSE, q("1", "year"), q("11", "months"))
    }

    @Test
    fun yearsAndMonthsAreNotEqualToDefiniteDurations() {
        // "unit conversion between variable length calendar durations (i.e. years and months) and
        // definite-time durations (i.e. days or below) results in null"
        assertEqual(null, q("1", "year"), q("365", "days"))
        assertEqual(null, q("1", "month"), q("30", "days"))
        assertEqual(null, q("1", "month"), q("4", "weeks"))
        // "UCUM definite-time duration quantities above days (and weeks) are not comparable to
        // calendar duration quantities above days (and weeks), and such comparisons return null"
        assertEqual(null, q("1", "month"), q("1", "mo"))
        assertEqual(null, q("1", "year"), q("1", "a"))
        assertEqual(null, q("1", "year"), q("12", "mo"))
    }

    @Test
    fun calendarDurationsAreNotEqualToOtherUnits() {
        assertEqual(null, q("1", "day"), q("1", "g"))
    }

    @Test
    fun calendarDurationsEquivalent() {
        // 1 year ~ 12 months, 1 year ~ 365 days, 1 month ~ 30 days, 1 week = 7 days, …
        assertEquivalent(Boolean.TRUE, q("1", "year"), q("12", "months"))
        assertEquivalent(Boolean.TRUE, q("1", "year"), q("365", "days"))
        assertEquivalent(Boolean.TRUE, q("1", "month"), q("30", "days"))
        assertEquivalent(Boolean.TRUE, q("1", "week"), q("7", "days"))
        assertEquivalent(Boolean.TRUE, q("1", "second"), q("1000", "milliseconds"))
        assertEquivalent(Boolean.TRUE, q("1", "year"), q("8760", "hours"))
        assertEquivalent(Boolean.FALSE, q("1", "year"), q("366", "days"))
        assertEquivalent(Boolean.FALSE, q("1", "month"), q("31", "days"))
        // Converted directly, not through months: 12 months is 360 days.
        assertEquivalent(Boolean.FALSE, q("12", "months"), q("365", "days"))
    }

    @Test
    fun calendarDurationsEquivalentToUcum() {
        // 1 year ~ 1 'a', 1 month ~ 1 'mo' (unchanged), and definite-time units convert.
        assertEquivalent(Boolean.TRUE, q("1", "year"), q("1", "a"))
        assertEquivalent(Boolean.TRUE, q("1", "month"), q("1", "mo"))
        assertEquivalent(Boolean.TRUE, q("7", "days"), q("1", "wk"))
        assertEquivalent(Boolean.FALSE, q("1", "day"), q("1", "g"))
    }

    @Test
    fun nullValues() {
        assertNull(
            EqualEvaluator.equal(Quantity().withValue(null).withUnit("week"), q("7", "days"), null)
        )
    }
}
