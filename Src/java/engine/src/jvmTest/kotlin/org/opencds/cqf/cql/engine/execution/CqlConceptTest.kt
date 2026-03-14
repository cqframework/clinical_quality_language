package org.opencds.cqf.cql.engine.execution

import java.io.IOException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType

internal class CqlConceptTest : CqlTestBase() {
    @Test
    @Throws(IOException::class)
    fun all_cql_concept_tests() {
        val environment = Environment(libraryManager)
        val engine = CqlEngine(environment)

        val results = engine.evaluate { library("CqlConceptTest") }.onlyResultOrThrow

        val codes = listOf(createCode("123", "1"), createCode("234", "1"), createCode("abc", "a"))
        val expected = Concept().withDisplay("test-concept-display").withCodes(codes)

        val actual = results["testConceptRef"]!!.value as CqlType?

        assertEqual(expected, actual)
    }

    companion object {
        private fun createCode(prefix: String?, systemVal: String?): Code {
            return Code()
                .withCode("$prefix-value")
                .withSystem("http://system-$systemVal.org")
                .withVersion(systemVal)
                .withDisplay("$prefix-display")
        }

        fun assertEqual(expected: CqlType, actual: CqlType?) {
            if (equal(expected, actual) != true) {
                val message = "Expected $expected but got $actual"
                Assertions.fail<Any?>(message)
            }
        }
    }
}
