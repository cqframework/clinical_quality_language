package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.AnyTrueEvaluator
import org.opencds.cqf.cql.engine.elm.executing.AvgEvaluator
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlAggregateFunctionsTest : CqlTestBase() {
    @Test
    fun all_aggregate_function_tests() {
        var value: Value?

        try {
            value =
                AnyTrueEvaluator.anyTrue(
                    mutableListOf("this".toCqlString(), "is".toCqlString(), "error".toCqlString())
                        .toCqlList()
                )
            Assertions.fail<Any?>()
        } catch (e: InvalidOperatorArgument) {
            // pass
        }

        try {
            value =
                AvgEvaluator.avg(
                    mutableListOf("this".toCqlString(), "is".toCqlString(), "error".toCqlString())
                        .toCqlList(),
                    engine.state,
                )
            Assertions.fail<Any?>()
        } catch (e: InvalidOperatorArgument) {
            // pass
        }
    }
}
