package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator

internal class LetClauseOutsideQueryContextTest : CqlTestBase() {
    @Test
    fun evaluate() {
        val results = engine.evaluate(toElmIdentifier("LetClauseOutsideQueryContextTest"))
        var value = results.forExpression("First Position of list")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], 1) == true
        )

        value =
            results.forExpression("Third Position of list With Same Name of Let As First")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], 3) == true
        )
    }
}
