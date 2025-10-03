package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.Tuple

internal class CqlQueryTest : CqlTestBase() {
    @Test
    fun all_query_operators() {
        val results = engine.evaluate(toElmIdentifier("CqlQueryTests"))
        var value = results.forExpression("RightShift")!!.value()
        Assertions.assertEquals(value, mutableListOf(null, "A", "B", "C"))
        value = results.forExpression("LeftShift")!!.value()
        Assertions.assertEquals(value, mutableListOf("B", "C", "D", null))
        value = results.forExpression("LeftShift2")!!.value()
        Assertions.assertEquals(value, mutableListOf("B", "C", "D", null))

        value = results.forExpression("Multisource")!!.value()
        Assertions.assertTrue(value is MutableList<*>)
        var list = value as MutableList<*>
        Assertions.assertEquals(1, list.size)
        Assertions.assertTrue(list[0] is Tuple)
        val resultTuple = list[0] as Tuple
        Assertions.assertTrue(
            resultTuple.elements.containsKey("A") && resultTuple.elements.containsKey("B")
        )

        value = results.forExpression("Complex Multisource")!!.value()
        Assertions.assertTrue(value is MutableList<*>)
        list = value as MutableList<*>
        Assertions.assertEquals(4, list.size)

        value = results.forExpression("Let Test Fails")!!.value()

        value = results.forExpression("Triple Source Query")!!.value()
        Assertions.assertTrue(value is MutableList<*>)
        list = value as MutableList<*>
        Assertions.assertEquals(27, list.size)

        value = results.forExpression("Let Expression in Multi Source Query")!!.value()
        Assertions.assertTrue(value is MutableList<*>)
        list = value as MutableList<*>
        Assertions.assertEquals(1, list.size)
        Assertions.assertTrue(EquivalentEvaluator.equivalent(list[0], 3) == true)

        value = results.forExpression("Accessing Third Element of Triple Source Query")!!.value()
        Assertions.assertTrue(value is MutableList<*>)
        list = value as MutableList<*>
        Assertions.assertEquals(1, list.size)
        Assertions.assertTrue(EquivalentEvaluator.equivalent(list[0], 3) == true)
    }

    @Test
    fun sort_by_fluent_function() {
        val results = engine.evaluate(toElmIdentifier("CqlQueryTests"))
        val value = results.forExpression("Sorted Tuples")!!.value()
        Assertions.assertInstanceOf(MutableList::class.java, value)
        val list = value as MutableList<*>
        Assertions.assertEquals(3, list.size)
        Assertions.assertInstanceOf(Tuple::class.java, list[0])
        var tuple = list[0] as Tuple
        Assertions.assertEquals(3, tuple.getElement("x"))
        tuple = list[1] as Tuple
        Assertions.assertEquals(2, tuple.getElement("x"))
        tuple = list[2] as Tuple
        Assertions.assertEquals(1, tuple.getElement("x"))
    }
}
