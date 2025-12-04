package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Interval

internal class Issue458 : CqlTestBase() {
    @Test
    fun interval() {
        val results = engine.evaluate { library("Issue458") }.onlyResultOrThrow
        val value = results["Closed-Open Interval"]!!.value
        val interval = value as Interval
        Assertions.assertEquals("Interval[3, 5)", interval.toString())
    }
}
