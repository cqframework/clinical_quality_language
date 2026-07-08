package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlComparisonOperatorsTest : CqlTestBase() {
    @Test
    fun all_comparison_operators_tests() {
        Assertions.assertThrows(CqlException::class.java) {
            GreaterEvaluator.greater(Integer.ONE, "one".toCqlString(), engine.state)
        }
    }
}
