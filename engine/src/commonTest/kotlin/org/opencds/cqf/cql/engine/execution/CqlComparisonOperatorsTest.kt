package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlString

class CqlComparisonOperatorsTest {
    @Test
    fun all_comparison_operators_tests() {
        assertFailsWith<CqlException> {
            GreaterEvaluator.greater(Integer.ONE, "one".toCqlString(), null)
        }
    }
}
