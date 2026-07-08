package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.Boolean

internal class Issue39 : CqlTestBase() {
    @Test
    fun interval() {
        val results = engine.evaluate { library("Issue39") }.onlyResultOrThrow
        val value = results["EquivalentIntervals"]!!.value
        assertEquals(Boolean.TRUE, value)
    }
}
