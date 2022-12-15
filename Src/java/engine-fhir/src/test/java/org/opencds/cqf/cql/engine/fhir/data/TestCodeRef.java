package org.opencds.cqf.cql.engine.fhir.data;

import static org.testng.AssertJUnit.assertTrue;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.fhir.terminology.Dstu3FhirTerminologyProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class TestCodeRef extends FhirExecutionTestBase {

    private IGenericClient fhirClient = FhirContext.forCached(FhirVersionEnum.DSTU3).newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");
    private Dstu3FhirTerminologyProvider terminologyProvider =
            new Dstu3FhirTerminologyProvider(fhirClient);

    // @Test
    public void CodeRefTest1() {
        Context context = new Context(library);
        context.registerTerminologyProvider(terminologyProvider);

        Object result = context.resolveExpressionRef("CodeRef1").getExpression().evaluate(context);
        assertTrue(result != null);
    }

    // @Test
    public void CodeRefTest2() {
        Context context = new Context(library);
        context.registerTerminologyProvider(terminologyProvider);

        Object result = context.resolveExpressionRef("CodeRef2").getExpression().evaluate(context);
        assertTrue(result != null);
    }
}
