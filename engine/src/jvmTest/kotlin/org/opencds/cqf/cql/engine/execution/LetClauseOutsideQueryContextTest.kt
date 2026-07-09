package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class LetClauseOutsideQueryContextTest : CqlTestBase() {
    @Test
    fun evaluate() {
        val results =
            engine.evaluate { library("LetClauseOutsideQueryContextTest") }.onlyResultOrThrow
        var value = results["First Position of list"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as List).elementAt(0), 1.toCqlInteger()).value ==
                true
        )

        value = results["Third Position of list With Same Name of Let As First"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as List).elementAt(0), 3.toCqlInteger()).value ==
                true
        )
    }
}
