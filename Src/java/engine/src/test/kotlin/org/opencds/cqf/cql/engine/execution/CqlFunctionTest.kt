package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class CqlFunctionTest : CqlTestBase() {
    @Test
    fun all_function_tests() {
        val compilerOptions =
            defaultOptions().withSignatureLevel(LibraryBuilder.SignatureLevel.Overloads)
        val engine = getEngine(compilerOptions)

        val results = engine.evaluate { library("CqlFunctionTests") }.onlyResultOrThrow
        var value = results.forExpression("FunctionTestStringArg")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("hello"))

        value = results.forExpression("FunctionTestNullStringArg")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("FunctionTestMultipleArgs")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("hell0"))

        value = results.forExpression("FunctionTestNullMultipleArgs")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("FunctionTestOverload")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("hell00.000"))

        value = results.forExpression("FunctionTestNullOverload")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("FunctionTestTupleArg")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(3))

        value = results.forExpression("FunctionTestNullTupleArg")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("FunctionTestQuantityArg")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("cm"))

        value = results.forExpression("FunctionTestNullQuantityArg")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))
    }
}
