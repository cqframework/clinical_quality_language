package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept

internal class CqlConceptTest : CqlTestBase() {
    @Test
    fun all_cql_concept_tests() {
        val environment = Environment(libraryManager)
        val engine = CqlEngine(environment)

        val results = engine.evaluate { library("CqlConceptTest") }.onlyResultOrThrow

        val codes = listOf(createCode("123", "1"), createCode("234", "1"), createCode("abc", "a"))
        val expected = Concept().withDisplay("test-concept-display").withCodes(codes)

        val actual = results["testConceptRef"]!!.value

        assertEquals(expected, actual)
    }

    companion object {
        private fun createCode(prefix: String?, systemVal: String?): Code {
            return Code()
                .withCode("$prefix-value")
                .withSystem("http://system-$systemVal.org")
                .withVersion(systemVal)
                .withDisplay("$prefix-display")
        }
    }
}
