package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.jupiter.api.Assertions.*;

import org.hl7.fhir.dstu2.model.Encounter;
import org.junit.jupiter.api.BeforeEach;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.fhir.retrieve.FhirBundleCursor;

public class TestFhirDataProviderDstu2 extends FhirExecutionTestBase {

    private EvaluationResult results;

    @BeforeEach
    void before() {
        CqlEngine engine = getEngine();
        engine.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu2Provider);
        results = engine.evaluate(library.getIdentifier());
        // BaseFhirDataProvider provider = new FhirDataProviderDstu2().setEndpoint("http://fhirtest.uhn.ca/baseDstu2");
        //        FhirDataProviderDstu2 primitiveProvider = new
        // FhirDataProviderDstu2().withEndpoint("http://fhirtest.uhn.ca/baseDstu2").withPackageName("ca.uhn.fhir.model.primitive");
        //        context.registerDataProvider("http://hl7.org/fhir", primitiveProvider);
        //        FhirDataProviderDstu2 compositeProvider = new
        // FhirDataProviderDstu2().withEndpoint("http://fhirtest.uhn.ca/baseDstu2").withPackageName("ca.uhn.fhir.model.dstu2.composite");
        //        context.registerDataProvider("http://hl7.org/fhir", compositeProvider);
    }

    // @Test
    public void testDstu2ProviderRetrieve() {
        String contextPath =
                dstu2ModelResolver.getContextPath("Patient", "Encounter").toString();
        FhirBundleCursor results = (FhirBundleCursor) dstu2Provider.retrieve(
                "Patient", contextPath, "2822", "Encounter", null, "code", null, null, null, null, null, null);

        for (Object result : results) {
            Encounter e = (Encounter) result;
            if (!e.getPatient().getIdElement().getIdPart().equals("2822")) {
                fail("Invalid patient id in Resource");
            }
        }

        assertTrue(true);
    }

    // @Test
    public void testDstu2ProviderString() {
        Object value = results.forExpression("testString").value();
        assertNotNull(value);
    }

    // @Test
    public void testDstu2ProviderCode() {
        Object value = results.forExpression("testCode").value();
        assertNotNull(value);
    }

    // @Test
    public void testDstu2ProviderDate() {
        Object value = results.forExpression("testDate").value();
        assertNotNull(value);
    }

    // @Test
    public void testDstu2ProviderDecimal() {
        Object value = results.forExpression("testDecimal").value();
        assertNotNull(value);
    }

    // @Test
    public void testDstu2ProviderID() {
        var value = results.forExpression("testID").value();
        assertNotNull(value);
    }
}
