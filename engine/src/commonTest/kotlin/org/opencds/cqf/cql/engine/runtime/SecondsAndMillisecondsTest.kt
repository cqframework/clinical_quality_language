package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.elm.executing.AfterEvaluator
import org.opencds.cqf.cql.engine.elm.executing.BeforeEvaluator
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator
import org.opencds.cqf.cql.engine.elm.executing.LessEvaluator
import org.opencds.cqf.cql.engine.elm.executing.SameAsEvaluator

/**
 * [#1962](https://github.com/cqframework/clinical_quality_language/issues/1962): seconds and
 * milliseconds are combined as a single precision, so a value given to seconds compares as one with
 * 0 milliseconds.
 */
class SecondsAndMillisecondsTest {
    private val utc = BigDecimal("0")

    private fun dateTime(vararg elements: Int) = DateTime(utc, *elements)

    private val seconds = dateTime(2024, 11, 15, 12, 30, 0)
    private val zeroMilliseconds = dateTime(2024, 11, 15, 12, 30, 0, 0)
    private val halfSecond = dateTime(2024, 11, 15, 12, 30, 0, 500)

    @Test
    fun dateTimeEqual() {
        assertEquals(Boolean.TRUE, EqualEvaluator.equal(zeroMilliseconds, seconds))
        assertEquals(Boolean.TRUE, EqualEvaluator.equal(seconds, zeroMilliseconds))
        assertEquals(Boolean.FALSE, EqualEvaluator.equal(halfSecond, seconds))
        assertEquals(Boolean.TRUE, EquivalentEvaluator.equivalent(zeroMilliseconds, seconds))
        assertEquals(Boolean.FALSE, EquivalentEvaluator.equivalent(halfSecond, seconds))
    }

    @Test
    fun dateTimeOrdering() {
        assertEquals(Boolean.TRUE, GreaterEvaluator.greater(halfSecond, seconds, null))
        assertEquals(Boolean.FALSE, GreaterEvaluator.greater(zeroMilliseconds, seconds, null))
        assertEquals(Boolean.FALSE, LessEvaluator.less(halfSecond, seconds, null))
        assertEquals(Boolean.TRUE, LessEvaluator.less(seconds, halfSecond, null))
        assertEquals(Boolean.TRUE, AfterEvaluator.after(halfSecond, seconds, null, null))
        assertEquals(Boolean.TRUE, BeforeEvaluator.before(seconds, halfSecond, null, null))
        assertEquals(Boolean.TRUE, SameAsEvaluator.sameAs(seconds, zeroMilliseconds, null, null))
    }

    @Test
    fun time() {
        val timeSeconds = Time(12, 30, 0)
        assertEquals(Boolean.TRUE, EqualEvaluator.equal(Time(12, 30, 0, 0), timeSeconds))
        assertEquals(Boolean.FALSE, EqualEvaluator.equal(Time(12, 30, 0, 1), timeSeconds))
        assertEquals(Boolean.TRUE, GreaterEvaluator.greater(Time(12, 30, 0, 1), timeSeconds, null))
        assertEquals(Boolean.TRUE, LessEvaluator.less(timeSeconds, Time(12, 30, 0, 1), null))
    }

    @Test
    fun coarserPrecisionsAreStillUncertain() {
        // A value given to minutes has no seconds, so the comparison is still null
        val minutes = dateTime(2024, 11, 15, 12, 30)
        assertNull(EqualEvaluator.equal(minutes, zeroMilliseconds))
        assertNull(EqualEvaluator.equal(minutes, seconds))
        assertEquals(Boolean.FALSE, EquivalentEvaluator.equivalent(minutes, seconds))
    }
}
