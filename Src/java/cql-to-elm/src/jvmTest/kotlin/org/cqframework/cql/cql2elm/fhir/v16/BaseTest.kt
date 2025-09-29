package org.cqframework.cql.cql2elm.fhir.v16

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils
import org.junit.jupiter.api.Test

internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun fhirHelpers() {
        TestUtils.runSemanticTest("fhir/v16/TestFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhir() {
        TestUtils.runSemanticTest("fhir/v16/TestFHIR.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithHelpers() {
        TestUtils.runSemanticTest("fhir/v16/TestFHIRWithHelpers.cql", 0)
    }
}
