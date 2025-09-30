package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator

internal class SortDescendingTest : CqlTestBase() {
    @Test
    fun evaluate() {
        val results = engine.evaluate(toElmIdentifier("SortDescendingTest"))
        val value = results.forExpression("sorted list of numbers descending").value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent((value as MutableList<*>)[0], 9))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[1], 4))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[2], 2))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[3], 1))
    }
}
