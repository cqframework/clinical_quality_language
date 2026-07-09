package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class TestUnion : CqlTestBase() {
    @Test
    fun union() {
        val results = engine.evaluate { library("TestUnion") }.onlyResultOrThrow

        var value = results["NullAndNullList"]!!.value
        assertNotNull(value)
        assertEquals(0, (value as List).count())

        value = results["NullAndNullInterval"]!!.value
        assertNull(value)

        value = results["NullAndNullUntyped"]!!.value
        assertNull(value)

        value = results["NullAndEmpty"]!!.value
        assertNotNull(value)
        assertEquals(0, (value as List).count())

        value = results["EmptyAndNull"]!!.value
        assertNotNull(value)
        assertEquals(0, (value as List).count())

        value = results["NullAndSingle"]!!.value
        assertNotNull(value)
        assertEquals(1, (value as List).count())
        assertEquals(1.toCqlInteger(), value.elementAt(0))

        value = results["SingleAndNull"]!!.value
        assertNotNull(value)
        assertEquals(1, (value as List).count())
        assertEquals(1.toCqlInteger(), value.elementAt(0))
    }
}
