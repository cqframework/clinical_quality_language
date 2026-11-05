package org.cqframework.cql.cql2elm.fhir.v18

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils.runSemanticTest
import org.junit.jupiter.api.Test

internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun fhirHelpers() {
        runSemanticTest("fhir/v18/TestFHIRHelpers.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhir() {
        runSemanticTest("fhir/v18/TestFHIR.cql", 0)
    }

    @Test
    @Throws(IOException::class)
    fun fhirWithHelpers() {
        runSemanticTest("fhir/v18/TestFHIRWithHelpers.cql", 0)
    }
}
