package org.cqframework.cql.cql2elm.fhir.v18;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.NamespaceInfo;
import org.cqframework.cql.cql2elm.TestUtils;
import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.TestUtils.visitFile;
import static org.cqframework.cql.cql2elm.TestUtils.visitFileLibrary;
import static org.cqframework.cql.cql2elm.matchers.QuickDataType.quickDataType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BaseTest {
    @BeforeClass
    public void Setup() {
        // Reset test utils to clear any models loaded by other tests
        TestUtils.reset();
    }

    @Test
    public void testFHIRHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v18/TestFHIRHelpers.cql", 0);
    }

    @Test
    public void testFHIR() throws IOException {
        TestUtils.runSemanticTest("fhir/v18/TestFHIR.cql", 0);
    }

    @Test
    public void testFHIRWithHelpers() throws IOException {
        TestUtils.runSemanticTest("fhir/v18/TestFHIRWithHelpers.cql", 0);
    }
}
