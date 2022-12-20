package org.opencds.cqf.cql.engine.fhir.data;

import static org.testng.Assert.assertTrue;

import org.hl7.fhir.dstu2.model.Encounter;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.fhir.retrieve.FhirBundleCursor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

public class TestFhirDataProviderDstu2 extends FhirExecutionTestBase {

    private Context context;

    @BeforeMethod
    public void before() {
        context = new Context(library);
        context.registerDataProvider("http://hl7.org/fhir", dstu2Provider);
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
        Object result = context.resolveExpressionRef("testString").getExpression().evaluate(context);
        assertTrue(result != null);
    }

    // @Test
    public void testDstu2ProviderCode() {
        Object result = context.resolveExpressionRef("testCode").getExpression().evaluate(context);
        assertTrue(result != null);
    }

    // @Test
    public void testDstu2ProviderDate() {
        Object result = context.resolveExpressionRef("testDate").getExpression().evaluate(context);
        assertTrue(result != null);
    }

    // @Test
    public void testDstu2ProviderDecimal() {
        Object result = context.resolveExpressionRef("testDecimal").getExpression().evaluate(context);
        assertTrue(result != null);
    }

    // @Test
    public void testDstu2ProviderID() {
        Object result = context.resolveExpressionRef("testID").getExpression().evaluate(context);
        assertTrue(result != null);
    }
}
