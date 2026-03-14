package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType

internal class IncludedConceptRefTest : CqlTestBase() {
    @Test
    fun included_concept_ref() {
        val code =
            Code()
                .withCode("code-value")
                .withDisplay("code-display")
                .withSystem("http://system.org")
                .withVersion("1")
        val expected =
            Concept().withDisplay("concept-display").withCodes(mutableListOf<Code?>(code))

        val results = engine.evaluate { library("IncludedConceptRefTest") }.onlyResultOrThrow
        val actual = results["testIncludedConceptRef"]!!.value as CqlType?

        CqlConceptTest.assertEqual(expected, actual)
    }
}
