package org.cqframework.cql.cql2elm.fhir.v14

import java.io.IOException
import org.cqframework.cql.cql2elm.TestUtils
import org.junit.jupiter.api.Test

internal class BaseTest {
    @Test
    @Throws(IOException::class)
    fun fhir() {
        TestUtils.runSemanticTest("fhir/v14/TestFHIR.cql", 0)
    }
}
