package org.cqframework.cql.cql2elm.fhir.v18;

import java.io.IOException;
import org.cqframework.cql.cql2elm.TestUtils;
import org.junit.jupiter.api.Test;

class BaseTest {
    @Test
    void fhirHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v18/TestFHIRHelpers.cql", 0);
    }

    @Test
    void fhir() throws IOException {
        TestUtils.runSemanticTest("fhir/v18/TestFHIR.cql", 0);
    }

    @Test
    void fhirWithHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v18/TestFHIRWithHelpers.cql", 0);
    }
}
