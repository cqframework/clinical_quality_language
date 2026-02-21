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
        var value = results["FunctionTestStringArg"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("hello"))

        value = results["FunctionTestNullStringArg"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["FunctionTestMultipleArgs"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("hell0"))

        value = results["FunctionTestNullMultipleArgs"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["FunctionTestOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("hell00.000"))

        value = results["FunctionTestNullOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["FunctionTestTupleArg"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(3))

        value = results["FunctionTestNullTupleArg"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["FunctionTestQuantityArg"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("cm"))

        value = results["FunctionTestNullQuantityArg"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))
    }
}
