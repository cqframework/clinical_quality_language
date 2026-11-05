package org.opencds.cqf.cql.engine.execution

import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.List

internal class Issue223 : CqlTestBase() {
    @Test
    fun interval() {
        val results = engine.evaluate { library("Issue223") }.onlyResultOrThrow
        var value = results["Access Flattened List of List Items"]!!.value
        assertIs<List>(value)
        assertEquals(1, value.count())
        assertEquals(Boolean.TRUE, value.elementAt(0))

        value = results["Access Flattened List of List Items in a Single Query"]!!.value
        assertIs<List>(value)
        assertEquals(1, value.count())
        assertEquals(Boolean.TRUE, value.elementAt(0))
    }
}
