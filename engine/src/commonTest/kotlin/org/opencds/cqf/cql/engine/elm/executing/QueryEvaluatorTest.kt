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
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList

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
            mutableListOf<Value?>(
                Tuple()
                    .withElements(
                        mutableMapOf("field1" to 2.toCqlInteger(), "field2" to 2.toCqlInteger())
                    ),
                Tuple()
                    .withElements(
                        mutableMapOf("field1" to 2.toCqlInteger(), "field2" to 1.toCqlInteger())
                    ),
                Tuple()
                    .withElements(
                        mutableMapOf("field1" to 2.toCqlInteger(), "field2" to 3.toCqlInteger())
                    ),
                Tuple()
                    .withElements(
                        mutableMapOf("field1" to 1.toCqlInteger(), "field2" to 2.toCqlInteger())
                    ),
                Tuple()
                    .withElements(
                        mutableMapOf("field1" to 1.toCqlInteger(), "field2" to 3.toCqlInteger())
                    ),
            )

        QueryEvaluator.sortResult(query, list, State(Environment(null)), EvaluationVisitor())

        val expected =
            listOf(
                    Tuple()
                        .withElements(
                            mutableMapOf("field1" to 1.toCqlInteger(), "field2" to 3.toCqlInteger())
                        ),
                    Tuple()
                        .withElements(
                            mutableMapOf("field1" to 1.toCqlInteger(), "field2" to 2.toCqlInteger())
                        ),
                    Tuple()
                        .withElements(
                            mutableMapOf("field1" to 2.toCqlInteger(), "field2" to 3.toCqlInteger())
                        ),
                    Tuple()
                        .withElements(
                            mutableMapOf("field1" to 2.toCqlInteger(), "field2" to 2.toCqlInteger())
                        ),
                    Tuple()
                        .withElements(
                            mutableMapOf("field1" to 2.toCqlInteger(), "field2" to 1.toCqlInteger())
                        ),
                )
                .toCqlList()

        assertEquals(expected, list.toCqlList())
    }
}
