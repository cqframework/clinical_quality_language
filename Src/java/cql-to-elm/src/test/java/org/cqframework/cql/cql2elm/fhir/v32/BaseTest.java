package org.cqframework.cql.cql2elm.fhir.v32;

import org.cqframework.cql.cql2elm.TestUtils;
import org.testng.annotations.Test;

import java.io.IOException;

public class BaseTest {
    @Test
    public void testFHIRHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v32/TestFHIRHelpers.cql", 0);
    }

    @Test
    public void testFHIR() throws IOException {
        TestUtils.runSemanticTest("fhir/v32/TestFHIR.cql", 0);
    }

    @Test
    public void testFHIRWithHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v32/TestFHIRWithHelpers.cql", 0);
    }
}
