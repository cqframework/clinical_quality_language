package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.opencds.cqf.cql.engine.elm.executing.AnyTrueEvaluator
import org.opencds.cqf.cql.engine.elm.executing.AvgEvaluator
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

class CqlAggregateFunctionsTest {
    @Test
    fun all_aggregate_function_tests() {
        assertFailsWith<InvalidOperatorArgument> {
            AnyTrueEvaluator.anyTrue(
                mutableListOf("this".toCqlString(), "is".toCqlString(), "error".toCqlString())
                    .toCqlList()
            )
        }

        assertFailsWith<InvalidOperatorArgument> {
            AvgEvaluator.avg(
                mutableListOf("this".toCqlString(), "is".toCqlString(), "error".toCqlString())
                    .toCqlList(),
                null,
            )
        }
    }
}
