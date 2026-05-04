package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.hl7.elm.r1.ByColumn
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.SortClause
import org.hl7.elm.r1.SortDirection
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.execution.EvaluationVisitor
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Tuple

class QueryEvaluatorTest {
    /**
     * [#1745](https://github.com/cqframework/clinical_quality_language/issues/1745): A secondary
     * sort in the opposite direction shouldn't reverse the primary sort
     */
    @Test
    fun sortResultTest() {
        val query =
            Query()
                .withSort(
                    SortClause()
                        .withBy(
                            listOf(
                                ByColumn().withPath("field1").withDirection(SortDirection.ASC),
                                ByColumn().withPath("field2").withDirection(SortDirection.DESC),
                            )
                        )
                )

        val list =
            mutableListOf<Any?>(
                Tuple().withElements(mutableMapOf("field1" to 2, "field2" to 2)),
                Tuple().withElements(mutableMapOf("field1" to 2, "field2" to 1)),
                Tuple().withElements(mutableMapOf("field1" to 2, "field2" to 3)),
                Tuple().withElements(mutableMapOf("field1" to 1, "field2" to 2)),
                Tuple().withElements(mutableMapOf("field1" to 1, "field2" to 3)),
            )

        QueryEvaluator.sortResult(query, list, State(Environment(null)), EvaluationVisitor())

        val expected =
            listOf<Any?>(
                Tuple().withElements(mutableMapOf("field1" to 1, "field2" to 3)),
                Tuple().withElements(mutableMapOf("field1" to 1, "field2" to 2)),
                Tuple().withElements(mutableMapOf("field1" to 2, "field2" to 3)),
                Tuple().withElements(mutableMapOf("field1" to 2, "field2" to 2)),
                Tuple().withElements(mutableMapOf("field1" to 2, "field2" to 1)),
            )

        assertEquals(true, EqualEvaluator.equal(expected, list))
    }
}
