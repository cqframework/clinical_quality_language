package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TestUnion : CqlTestBase() {
    @Test
    fun union() {
        val results = engine.evaluate(toElmIdentifier("TestUnion"))

        var value = results.forExpression("NullAndNullList")!!.value()
        Assertions.assertNotNull(value)
        Assertions.assertTrue((value as MutableList<*>).isEmpty())

        value = results.forExpression("NullAndNullInterval")!!.value()
        Assertions.assertNull(value)

        value = results.forExpression("NullAndNullUntyped")!!.value()
        Assertions.assertNull(value)

        value = results.forExpression("NullAndEmpty")!!.value()
        Assertions.assertNotNull(value)
        Assertions.assertTrue((value as MutableList<*>).isEmpty())

        value = results.forExpression("EmptyAndNull")!!.value()
        Assertions.assertNotNull(value)
        Assertions.assertTrue((value as MutableList<*>).isEmpty())

        value = results.forExpression("NullAndSingle")!!.value()
        Assertions.assertNotNull(value)
        Assertions.assertEquals(1, (value as MutableList<*>).size)
        Assertions.assertEquals(1, value[0])

        value = results.forExpression("SingleAndNull")!!.value()
        Assertions.assertNotNull(value)
        Assertions.assertEquals(1, (value as MutableList<*>).size)
        Assertions.assertEquals(1, value[0])
    }
}
