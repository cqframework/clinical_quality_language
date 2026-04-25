package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlQueryTest : CqlTestBase() {
    @Test
    fun all_query_operators() {
        val results = engine.evaluate { library("CqlQueryTests") }.onlyResultOrThrow
        var value = results["RightShift"]!!.value
        assertEquals(
            mutableListOf(null, "A".toCqlString(), "B".toCqlString(), "C".toCqlString())
                .toCqlList(),
            value,
        )
        value = results["LeftShift"]!!.value
        assertEquals(
            mutableListOf("B".toCqlString(), "C".toCqlString(), "D".toCqlString(), null)
                .toCqlList(),
            value,
        )
        value = results["LeftShift2"]!!.value
        assertEquals(
            mutableListOf("B".toCqlString(), "C".toCqlString(), "D".toCqlString(), null)
                .toCqlList(),
            value,
        )

        value = results["Multisource"]!!.value
        assertIs<List>(value)
        var list = value
        assertEquals(1, list.count())
        assertIs<Tuple>(list.elementAt(0))
        val resultTuple = list.elementAt(0) as Tuple
        assertTrue(resultTuple.elements.containsKey("A") && resultTuple.elements.containsKey("B"))

        value = results["Complex Multisource"]!!.value
        assertIs<List>(value)
        list = value
        Assertions.assertEquals(4, list.count())

        value = results["Let Test Fails"]!!.value

        value = results["Triple Source Query"]!!.value
        assertIs<List>(value)
        list = value
        Assertions.assertEquals(27, list.count())

        value = results["Let Expression in Multi Source Query"]!!.value
        assertIs<List>(value)
        list = value
        Assertions.assertEquals(1, list.count())
        assertTrue(
            EquivalentEvaluator.equivalent(list.elementAt(0), 3.toCqlInteger()).value == true
        )

        value = results["Accessing Third Element of Triple Source Query"]!!.value
        assertIs<List>(value)
        list = value
        assertEquals(1, list.count())
        assertTrue(
            EquivalentEvaluator.equivalent(list.elementAt(0), 3.toCqlInteger()).value == true
        )
    }

    @Test
    fun sort_by_fluent_function() {
        val results = engine.evaluate { library("CqlQueryTests") }.onlyResultOrThrow
        val value = results["Sorted Tuples"]!!.value
        assertIs<List>(value)
        val list = value
        assertEquals(3, list.count())
        assertIs<Tuple>(list.elementAt(0))
        var tuple = list.elementAt(0) as Tuple
        assertEquals(3.toCqlInteger(), tuple.getElement("x"))
        tuple = list.elementAt(1) as Tuple
        assertEquals(2.toCqlInteger(), tuple.getElement("x"))
        tuple = list.elementAt(2) as Tuple
        assertEquals(1.toCqlInteger(), tuple.getElement("x"))
    }
}
