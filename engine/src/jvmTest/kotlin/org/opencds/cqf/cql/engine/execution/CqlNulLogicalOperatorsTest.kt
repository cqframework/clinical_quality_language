package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlNulLogicalOperatorsTest : CqlTestBase() {
    @Test
    fun all_null_logical_operators() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset
        val results = engine.evaluate { library("CqlNullologicalOperatorsTest") }.onlyResultOrThrow
        var value = results["CoalesceANull"]!!.value
        assertEquals("a".toCqlString(), value)

        value = results["CoalesceNullA"]!!.value
        assertEquals("a".toCqlString(), value)

        value = results["CoalesceEmptyList"]!!.value
        assertNull(value)

        value = results["CoalesceListFirstA"]!!.value
        assertEquals("a".toCqlString(), value)

        value = results["CoalesceListLastA"]!!.value
        assertEquals("a".toCqlString(), value)

        value = results["CoalesceFirstList"]!!.value
        assertEquals(listOf("a".toCqlString()).toCqlList(), value)

        value = results["CoalesceLastList"]!!.value
        assertEquals(listOf("a".toCqlString()).toCqlList(), value)

        value = results["DateTimeCoalesce"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 18))
                .value == true
        )

        value = results["DateTimeListCoalesce"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 18))
                .value == true
        )

        value = results["TimeCoalesce"]!!.value
        assertTrue(EquivalentEvaluator.equivalent(value, Time(5, 15, 33, 556)).value == true)

        value = results["TimeListCoalesce"]!!.value
        assertTrue(EquivalentEvaluator.equivalent(value, Time(5, 15, 33, 556)).value == true)

        value = results["IsNullTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IsNullFalseEmptyString"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IsNullAlsoFalseAbcString"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IsNullAlsoFalseNumber1"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IsNullAlsoFalseNumberZero"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IsFalseFalse"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IsFalseTrue"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IsFalseNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IsTrueTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IsTrueFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IsTrueNull"]!!.value
        assertEquals(Boolean.FALSE, value)
    }
}
