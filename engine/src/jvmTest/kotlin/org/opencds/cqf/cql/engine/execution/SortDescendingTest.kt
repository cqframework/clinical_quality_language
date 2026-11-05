package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class SortDescendingTest : CqlTestBase() {
    @Test
    fun evaluate() {
        val results = engine.evaluate { library("SortDescendingTest") }.onlyResultOrThrow
        val value = results["sorted list of numbers descending"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as List).elementAt(0), 9.toCqlInteger()).value ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(1), 4.toCqlInteger()).value == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(2), 2.toCqlInteger()).value == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(3), 1.toCqlInteger()).value == true
        )
    }
}
