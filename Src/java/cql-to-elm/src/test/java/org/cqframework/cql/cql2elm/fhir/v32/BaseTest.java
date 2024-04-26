package org.cqframework.cql.cql2elm.fhir.v32;

import java.io.IOException;
import org.cqframework.cql.cql2elm.TestUtils;
import org.junit.jupiter.api.Test;

class BaseTest {
    @Test
    void fhirHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v32/TestFHIRHelpers.cql", 0);
    }

    @Test
    void fhir() throws IOException {
        TestUtils.runSemanticTest("fhir/v32/TestFHIR.cql", 0);
    }

    @Test
    void fhirWithHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v32/TestFHIRWithHelpers.cql", 0);
    }
}
