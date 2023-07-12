package org.cqframework.cql.cql2elm.fhir.v14;

import org.cqframework.cql.cql2elm.TestUtils;
import org.testng.annotations.Test;

import java.io.IOException;

public class BaseTest {
    @Test
    public void testFHIR() throws IOException {
        TestUtils.runSemanticTest("fhir/v14/TestFHIR.cql", 0);
    }
}
