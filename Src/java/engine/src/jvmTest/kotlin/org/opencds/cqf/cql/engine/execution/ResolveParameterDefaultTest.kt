package org.opencds.cqf.cql.engine.execution

import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.cqframework.cql.cql2elm.CqlIncludeException
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/**
 * Tests for [CqlEngine.resolveParameterDefault].
 *
 * These tests mirror the scenario in clinical-reasoning's
 * `MeasureProcessorTimeUtils.resolveDefaultMeasurementPeriodWithLibraryStack`, which resolves the
 * CQL-defined default "Measurement Period" before measure evaluation. The new API replaces the
 * manual library-stack push/pop ceremony with a single engine call.
 */
internal class ResolveParameterDefaultTest : CqlTestBase() {

    /**
     * Core scenario from clinical-reasoning: resolve the default "Measurement Period" which is an
     * `Interval<DateTime>`. Verifies the returned value is an Interval with the expected DateTime
     * boundaries.
     */
    @Test
    fun resolves_measurement_period_interval_default() {
        val result = engine.resolveParameterDefault(LIBRARY_ID, "Measurement Period")

        assertNotNull(result)
        assertIs<Interval>(result)

        val interval = result
        assertIs<DateTime>(interval.start)
        assertIs<DateTime>(interval.end)

        val start = interval.start as DateTime
        assertEquals(2020, start.dateTime!!.year)
        assertEquals(1, start.dateTime!!.monthValue)
        assertEquals(1, start.dateTime!!.dayOfMonth)
        assertEquals(0, start.dateTime!!.hour)
        assertEquals(0, start.dateTime!!.minute)

        val end = interval.end as DateTime
        assertEquals(2020, end.dateTime!!.year)
        assertEquals(12, end.dateTime!!.monthValue)
        assertEquals(31, end.dateTime!!.dayOfMonth)
        assertEquals(23, end.dateTime!!.hour)
        assertEquals(59, end.dateTime!!.minute)
    }

    /** Parameter declared without a default expression should return null. */
    @Test
    fun returns_null_for_parameter_without_default() {
        val result = engine.resolveParameterDefault(LIBRARY_ID, "No Default Param")

        assertNull(result)
    }

    /** Resolving a non-Interval parameter with a simple literal default. */
    @Test
    fun resolves_integer_default() {
        val result = engine.resolveParameterDefault(LIBRARY_ID, "Integer Default")

        assertEquals(42.toCqlInteger(), result)
    }

    /** A parameter whose default is `Now()` should respect the provided evaluationDateTime. */
    @Test
    fun respects_evaluation_date_time_for_now_default() {
        val evalTime = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)

        val result = engine.resolveParameterDefault(LIBRARY_ID, "Now Default", evalTime)

        assertNotNull(result)
        assertIs<DateTime>(result)

        val dateTime = result
        assertEquals(2023, dateTime.dateTime!!.year)
        assertEquals(6, dateTime.dateTime!!.monthValue)
        assertEquals(15, dateTime.dateTime!!.dayOfMonth)
    }

    /** Requesting a parameter name that does not exist in the library should throw. */
    @Test
    fun throws_for_nonexistent_parameter() {
        assertFailsWith<CqlException> {
            engine.resolveParameterDefault(LIBRARY_ID, "Nonexistent Parameter")
        }
    }

    /**
     * Requesting a library that cannot be resolved should throw. The library manager throws
     * [CqlIncludeException] when the library source cannot be found.
     */
    @Test
    fun throws_for_nonexistent_library() {
        assertFailsWith<CqlIncludeException> {
            engine.resolveParameterDefault(toElmIdentifier("NonexistentLibrary"), "Some Parameter")
        }
    }

    /**
     * Verifies that calling resolveParameterDefault does not corrupt engine state: a subsequent
     * evaluate() call on the same engine instance should produce correct results. This mirrors the
     * clinical-reasoning usage where default resolution precedes full measure evaluation.
     */
    @Test
    fun does_not_interfere_with_subsequent_evaluate() {
        val defaultPeriod = engine.resolveParameterDefault(LIBRARY_ID, "Measurement Period")
        assertNotNull(defaultPeriod)

        val result =
            engine
                .evaluate { library(LIBRARY_ID) { expressions("Simple Expression") } }
                .onlyResultOrThrow

        assertEquals(42.toCqlInteger(), result["Simple Expression"]!!.value)
    }

    /**
     * Verifies that calling evaluate() first, then resolveParameterDefault, then evaluate() again
     * all produce correct results. This ensures the lifecycle cleanup is fully symmetric.
     */
    @Test
    fun works_between_two_evaluate_calls() {
        val result1 =
            engine
                .evaluate { library(LIBRARY_ID) { expressions("Simple Expression") } }
                .onlyResultOrThrow
        assertEquals(42.toCqlInteger(), result1["Simple Expression"]!!.value)

        val defaultPeriod = engine.resolveParameterDefault(LIBRARY_ID, "Measurement Period")
        assertNotNull(defaultPeriod)
        assertIs<Interval>(defaultPeriod)

        val result2 =
            engine
                .evaluate { library(LIBRARY_ID) { expressions("Simple Expression") } }
                .onlyResultOrThrow
        assertEquals(42.toCqlInteger(), result2["Simple Expression"]!!.value)
    }

    companion object {
        private val LIBRARY_ID = toElmIdentifier("ResolveParameterDefaultTest")
    }
}
