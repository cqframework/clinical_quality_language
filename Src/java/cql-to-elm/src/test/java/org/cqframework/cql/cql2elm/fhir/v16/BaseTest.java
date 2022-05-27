package org.cqframework.cql.cql2elm.fhir.v16;

import org.cqframework.cql.cql2elm.TestUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.cqframework.cql.cql2elm.TestUtils.visitFile;
import static org.cqframework.cql.cql2elm.matchers.QuickDataType.quickDataType;
import static org.hamcrest.MatcherAssert.assertThat;

public class BaseTest {
    @BeforeClass
    public void Setup() {
        // Reset test utils to clear any models loaded by other tests
        TestUtils.reset();
    }

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
