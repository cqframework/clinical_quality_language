package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TestUnion : CqlTestBase() {
    @Test
    fun union() {
        val results = engine.evaluate { library("TestUnion") }.onlyResultOrThrow

        var value = results["NullAndNullList"]!!.value
        Assertions.assertNotNull(value)
        Assertions.assertTrue((value as MutableList<*>).isEmpty())

        value = results["NullAndNullInterval"]!!.value
        Assertions.assertNull(value)

        value = results["NullAndNullUntyped"]!!.value
        Assertions.assertNull(value)

        value = results["NullAndEmpty"]!!.value
        Assertions.assertNotNull(value)
        Assertions.assertTrue((value as MutableList<*>).isEmpty())

        value = results["EmptyAndNull"]!!.value
        Assertions.assertNotNull(value)
        Assertions.assertTrue((value as MutableList<*>).isEmpty())

        value = results["NullAndSingle"]!!.value
        Assertions.assertNotNull(value)
        Assertions.assertEquals(1, (value as MutableList<*>).size)
        Assertions.assertEquals(1, value[0])

        value = results["SingleAndNull"]!!.value
        Assertions.assertNotNull(value)
        Assertions.assertEquals(1, (value as MutableList<*>).size)
        Assertions.assertEquals(1, value[0])
    }
}
