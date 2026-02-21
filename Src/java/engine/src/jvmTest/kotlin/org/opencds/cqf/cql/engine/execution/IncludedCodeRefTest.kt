package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Code

internal class IncludedCodeRefTest : CqlTestBase() {
    @Test
    fun included_code_ref() {
        val results = engine.evaluate { library("IncludedCodeRefTest") }.onlyResultOrThrow
        val value = results["IncludedCode"]!!.value
        Assertions.assertNotNull(value)
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.instanceOf(Code::class.java)))
    }
}
