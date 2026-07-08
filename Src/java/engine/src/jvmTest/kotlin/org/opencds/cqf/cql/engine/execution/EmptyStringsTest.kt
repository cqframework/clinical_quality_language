package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class EmptyStringsTest : CqlTestBase() {
    @Test
    fun all_empty_string() {
        val results = engine.evaluate { library("EmptyStringsTest") }.onlyResultOrThrow
        var value = results["Null"]!!.value
        assertNull(value)

        value = results["Space"]!!.value
        assertEquals(" ".toCqlString(), value)

        value = results["Empty"]!!.value
        assertEquals(String.EMPTY_STRING, value)
    }
}
