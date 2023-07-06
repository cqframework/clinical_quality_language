package org.cqframework.cql.cql2elm.fhir.v16;

import org.cqframework.cql.cql2elm.TestUtils;
import org.testng.annotations.Test;

import java.io.IOException;

public class BaseTest {
    @Test
    public void testFHIRHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v16/TestFHIRHelpers.cql", 0);
    }

    @Test
    public void testFHIR() throws IOException {
        TestUtils.runSemanticTest("fhir/v16/TestFHIR.cql", 0);
    }

    @Test
    public void testFHIRWithHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v16/TestFHIRWithHelpers.cql", 0);
    }
}
