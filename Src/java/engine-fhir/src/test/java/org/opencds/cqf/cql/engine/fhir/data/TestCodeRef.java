package org.opencds.cqf.cql.engine.fhir.data;

import static org.testng.AssertJUnit.assertTrue;

import org.hl7.fhirpath.TranslatorHelper;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.fhir.terminology.Dstu3FhirTerminologyProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import java.util.Set;

public class TestCodeRef extends FhirExecutionTestBase {

    private IGenericClient fhirClient = FhirContext.forCached(FhirVersionEnum.DSTU3).newRestfulGenericClient("http://measure.eval.kanvix.com/cqf-ruler/baseDstu3");
    private Dstu3FhirTerminologyProvider terminologyProvider =
            new Dstu3FhirTerminologyProvider(fhirClient);

    // @Test
    public void CodeRefTest1() {
        CqlEngine engineVisitor = TranslatorHelper.getEngineVisitor();
        engineVisitor.getEnvironment().setTerminologyProvider(terminologyProvider);

        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("CodeRef1"), null, null, null, null);

        assertTrue(evaluationResult.expressionResults.get("CodeRef1").value() != null);
    }

    // @Test
    public void CodeRefTest2() {
        CqlEngine engineVisitor = TranslatorHelper.getEngineVisitor();
        engineVisitor.getEnvironment().setTerminologyProvider(terminologyProvider);

        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("CodeRef2"), null, null, null, null);

        assertTrue(evaluationResult.expressionResults.get("CodeRef2").value() != null);
    }
}
