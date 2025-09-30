package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator

internal class Issue208 : CqlTestBase() {
    @Test
    fun interval() {
        val results = engine.evaluate(toElmIdentifier("Issue208"))
        var value = results.forExpression("Let Test 1").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((((value as MutableList<*>)[0]) as MutableList<*>)[0], 1)
        )
        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[0]) as MutableList<*>)[1], 2))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[0]) as MutableList<*>)[2], 3))

        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[0], 4))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[1], 5))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[2], 6))

        value = results.forExpression("Let Test 2").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((((value as MutableList<*>)[0]) as MutableList<*>)[0], 1)
        )
        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[0]) as MutableList<*>)[1], 2))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[0]) as MutableList<*>)[2], 3))

        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[0], 4))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[1], 5))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[2], 6))
    }
}
