package org.opencds.cqf.cql.engine.fhir.data;

import static org.testng.Assert.assertTrue;

import org.hl7.fhir.dstu2.model.Encounter;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.fhir.retrieve.FhirBundleCursor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

public class TestFhirDataProviderDstu2 extends FhirExecutionTestBase {

    private EvaluationResult evaluationResult;

    @BeforeMethod
    public void before() {
        CqlEngine engineVisitor = getEngine();
        engineVisitor.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu2Provider);
        evaluationResult = engineVisitor.evaluate(library.getIdentifier(),
                null, null, null, null, null);
        //BaseFhirDataProvider provider = new FhirDataProviderDstu2().setEndpoint("http://fhirtest.uhn.ca/baseDstu2");
//        FhirDataProviderDstu2 primitiveProvider = new FhirDataProviderDstu2().withEndpoint("http://fhirtest.uhn.ca/baseDstu2").withPackageName("ca.uhn.fhir.model.primitive");
//        context.registerDataProvider("http://hl7.org/fhir", primitiveProvider);
//        FhirDataProviderDstu2 compositeProvider = new FhirDataProviderDstu2().withEndpoint("http://fhirtest.uhn.ca/baseDstu2").withPackageName("ca.uhn.fhir.model.dstu2.composite");
//        context.registerDataProvider("http://hl7.org/fhir", compositeProvider);
    }

    //@Test
    public void testDstu2ProviderRetrieve() {
		String contextPath = dstu2ModelResolver.getContextPath("Patient", "Encounter").toString();
        FhirBundleCursor results = (FhirBundleCursor) dstu2Provider.retrieve("Patient", contextPath, "2822", "Encounter", null, "code", null, null, null, null, null, null);

        for (Object result : results) {
            Encounter e = (Encounter) result;
            if (!e.getPatient().getIdElement().getIdPart().equals("2822")) {
                Assert.fail("Invalid patient id in Resource");
            }
        }

        assertTrue(true);
    }

    // @Test
    public void testDstu2ProviderString() {
        Object result = evaluationResult.expressionResults.get("testString").value();
        assertTrue(result != null);
    }

    // @Test
    public void testDstu2ProviderCode() {
        Object result = evaluationResult.expressionResults.get("testCode").value();
        assertTrue(result != null);
    }

    // @Test
    public void testDstu2ProviderDate() {
        Object result = evaluationResult.expressionResults.get("testDate").value();
        assertTrue(result != null);
    }

    // @Test
    public void testDstu2ProviderDecimal() {
        Object result = evaluationResult.expressionResults.get("testDecimal").value();
        assertTrue(result != null);
    }

    // @Test
    public void testDstu2ProviderID() {
        Object result = evaluationResult.expressionResults.get("testID").value();
        assertTrue(result != null);
    }
}
