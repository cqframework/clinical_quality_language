package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.Boolean

internal class DateComparatorTest : CqlTestBase() {
    @Test
    fun date_comparator() {
        val results = engine.evaluate { library("DateComparatorTest") }.onlyResultOrThrow
        val value = results["Date Comparator Test"]!!.value
        assertEquals(Boolean.TRUE, value)
    }
}
