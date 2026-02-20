package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.AnyTrueEvaluator
import org.opencds.cqf.cql.engine.elm.executing.AvgEvaluator
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

internal class CqlAggregateFunctionsTest : CqlTestBase() {
    @Test
    fun all_aggregate_function_tests() {
        var value: Any?

        try {
            value = AnyTrueEvaluator.anyTrue(mutableListOf<String?>("this", "is", "error"))
            Assertions.fail<Any?>()
        } catch (e: InvalidOperatorArgument) {
            // pass
        }

        try {
            value = AvgEvaluator.avg(mutableListOf<String?>("this", "is", "error"), engine.state)
            Assertions.fail<Any?>()
        } catch (e: InvalidOperatorArgument) {
            // pass
        }
    }
}
