package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlFunctionTest : CqlTestBase() {
    @Test
    fun all_function_tests() {
        val compilerOptions =
            defaultOptions().withSignatureLevel(LibraryBuilder.SignatureLevel.Overloads)
        val engine = getEngine(compilerOptions)

        val results = engine.evaluate { library("CqlFunctionTests") }.onlyResultOrThrow
        var value = results["FunctionTestStringArg"]!!.value
        assertEquals("hello".toCqlString(), value)

        value = results["FunctionTestNullStringArg"]!!.value
        assertNull(value)

        value = results["FunctionTestMultipleArgs"]!!.value
        assertEquals("hell0".toCqlString(), value)

        value = results["FunctionTestNullMultipleArgs"]!!.value
        assertNull(value)

        value = results["FunctionTestOverload"]!!.value
        assertEquals("hell00.000".toCqlString(), value)

        value = results["FunctionTestNullOverload"]!!.value
        assertNull(value)

        value = results["FunctionTestTupleArg"]!!.value
        assertEquals(3.toCqlInteger(), value)

        value = results["FunctionTestNullTupleArg"]!!.value
        assertNull(value)

        value = results["FunctionTestQuantityArg"]!!.value
        assertEquals("cm".toCqlString(), value)

        value = results["FunctionTestNullQuantityArg"]!!.value
        assertNull(value)
    }
}
