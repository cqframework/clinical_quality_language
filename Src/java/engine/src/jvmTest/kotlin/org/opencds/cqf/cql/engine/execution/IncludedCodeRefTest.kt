package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertIs
import org.opencds.cqf.cql.engine.runtime.Code

internal class IncludedCodeRefTest : CqlTestBase() {
    @Test
    fun included_code_ref() {
        val results = engine.evaluate { library("IncludedCodeRefTest") }.onlyResultOrThrow
        val value = results["IncludedCode"]!!.value
        assertIs<Code>(value)
    }
}
