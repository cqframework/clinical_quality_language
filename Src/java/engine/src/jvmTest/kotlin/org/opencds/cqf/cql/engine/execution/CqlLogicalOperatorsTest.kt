package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.Boolean

internal class CqlLogicalOperatorsTest : CqlTestBase() {
    @Test
    fun all_logical_operators() {
        val results = engine.evaluate { library("CqlLogicalOperatorsTest") }.onlyResultOrThrow
        var value = results["TrueAndTrue"]!!.value
        assertEquals(Boolean.TRUE, value)
        assertEquals(Boolean.TRUE, value)

        value = results["TrueAndFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TrueAndNull"]!!.value
        assertNull(value)

        value = results["FalseAndTrue"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["FalseAndFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["FalseAndNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["NullAndTrue"]!!.value
        assertNull(value)

        value = results["NullAndFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["NullAndNull"]!!.value
        assertNull(value)

        value = results["NotTrue"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["NotFalse"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["NotNull"]!!.value
        assertNull(value)

        value = results["TrueOrTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TrueOrFalse"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TrueOrNull"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["FalseOrTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["FalseOrFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["FalseOrNull"]!!.value
        assertNull(value)

        value = results["NullOrTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["NullOrFalse"]!!.value
        assertNull(value)

        value = results["NullOrNull"]!!.value
        assertNull(value)

        value = results["TrueXorTrue"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TrueXorFalse"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TrueXorNull"]!!.value
        assertNull(value)

        value = results["FalseXorTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["FalseXorFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["FalseXorNull"]!!.value
        assertNull(value)

        value = results["NullXorTrue"]!!.value
        assertNull(value)

        value = results["NullXorFalse"]!!.value
        assertNull(value)

        value = results["NullXorNull"]!!.value
        assertNull(value)
    }
}
