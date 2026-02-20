package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator

internal class Issue208 : CqlTestBase() {
    @Test
    fun interval() {
        val results = engine.evaluate { library("Issue208") }.onlyResultOrThrow
        var value = results["Let Test 1"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (((value as MutableList<*>)[0]) as MutableList<*>)[0],
                1,
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[0]) as MutableList<*>)[1], 2) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[0]) as MutableList<*>)[2], 3) == true
        )

        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[0], 4) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[1], 5) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[2], 6) == true
        )

        value = results["Let Test 2"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (((value as MutableList<*>)[0]) as MutableList<*>)[0],
                1,
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[0]) as MutableList<*>)[1], 2) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[0]) as MutableList<*>)[2], 3) == true
        )

        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[0], 4) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[1], 5) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(((value[1]) as MutableList<*>)[2], 6) == true
        )
    }
}
