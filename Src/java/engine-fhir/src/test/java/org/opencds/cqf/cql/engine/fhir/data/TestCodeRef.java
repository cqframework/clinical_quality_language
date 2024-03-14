package org.opencds.cqf.cql.engine.fhir.data;

import static org.testng.AssertJUnit.assertTrue;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Set;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.fhir.terminology.Dstu3FhirTerminologyProvider;

public class TestCodeRef extends FhirExecutionTestBase {

    private IGenericClient fhirClient = FhirContext.forCached(FhirVersionEnum.DSTU3)
            .newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");
    private Dstu3FhirTerminologyProvider terminologyProvider = new Dstu3FhirTerminologyProvider(fhirClient);

    // @Test
    public void CodeRefTest1() {
        CqlEngine engine = getEngine();

        EvaluationResult evaluationResult =
                engine.evaluate(library.getIdentifier(), Set.of("CodeRef1"));

        assertTrue(evaluationResult.forExpression("CodeRef1").value() != null);
    }

    // @Test
    public void CodeRefTest2() {
        CqlEngine engine = getEngine();

        EvaluationResult evaluationResult =
                engine.evaluate(library.getIdentifier(), Set.of("CodeRef2"));

        assertTrue(evaluationResult.forExpression("CodeRef2").value() != null);
    }
}
