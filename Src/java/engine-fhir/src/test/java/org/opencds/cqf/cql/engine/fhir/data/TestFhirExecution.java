package org.opencds.cqf.cql.engine.fhir.data;

import java.util.List;
import java.util.Set;

import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.testng.Assert;

public class TestFhirExecution extends FhirExecutionTestBase {

    // TODO: fix this... I think it requires a resource to be loaded - put in init bundle
    //@Test
    public void testCoalesce() {
        CqlEngine engineVisitor = getEngine();
        engineVisitor.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(),
                Set.of("testCoalesce"), null, null, null, null);

        Object result = evaluationResult.expressionResults.get("testCoalesce").value();
        Assert.assertTrue((Integer)((List<?>) result).get(0) == 72);
    }

    // @Test
    public void testMonthFrom() {
        CqlEngine engineVisitor = getEngine();
        engineVisitor.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        engineVisitor.getState().setParameter(null, "MAXYEAR", 2014);
        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(),
                Set.of("testMonthFrom"), null, null, null, null);
        Object result = evaluationResult.expressionResults.get("testMonthFrom").value();
        Assert.assertTrue(result != null);
    }

    // @Test
    public void testMultisourceQueryCreatingDatePeriod() {
        CqlEngine engineVisitor = getEngine();
        engineVisitor.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(),
                Set.of("Immunizations in range"), null, null, null, null);
        Object result = evaluationResult.expressionResults.get("Immunizations in range").value();
        Assert.assertTrue(result != null);
    }

    // @Test
    public void testIdResolution() {
        CqlEngine engineVisitor = getEngine();
        engineVisitor.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(),
                Set.of("Resource Id"), null, null, null, null);
        Object result = evaluationResult.expressionResults.get("Resource Id").value();
        Assert.assertTrue(result != null);
    }
}
