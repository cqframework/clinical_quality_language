package org.opencds.cqf.cql.engine.fhir.data;

import java.util.List;
import java.util.Set;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.testng.Assert;

public class TestFhirExecution extends FhirExecutionTestBase {

    // TODO: fix this... I think it requires a resource to be loaded - put in init bundle
    // @Test
    public void testCoalesce() {
        CqlEngine engine = getEngine();
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        EvaluationResult evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("testCoalesce"));

        Object result = evaluationResult.forExpression("testCoalesce").value();
        Assert.assertTrue((Integer) ((List<?>) result).get(0) == 72);
    }

    // @Test
    public void testMonthFrom() {
        CqlEngine engine = getEngine();
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        engine.getState().setParameter(null, "MAXYEAR", 2014);
        EvaluationResult evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("testMonthFrom"));
        Object result = evaluationResult.forExpression("testMonthFrom").value();
        Assert.assertTrue(result != null);
    }

    // @Test
    public void testMultisourceQueryCreatingDatePeriod() {
        CqlEngine engine = getEngine();
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        EvaluationResult evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("Immunizations in range"));
        Object result = evaluationResult.forExpression("Immunizations in range").value();
        Assert.assertTrue(result != null);
    }

    // @Test
    public void testIdResolution() {
        CqlEngine engine = getEngine();
        engine.getEnvironment().registerDataProvider("http://hl7.org/fhir", dstu3Provider);
        EvaluationResult evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("Resource Id"));
        Object result = evaluationResult.forExpression("Resource Id").value();
        Assert.assertTrue(result != null);
    }
}
