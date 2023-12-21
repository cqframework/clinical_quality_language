package org.opencds.cqf.cql.engine.fhir.data;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Set;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.Test;

public class EvaluatedResourcesTest extends FhirExecutionTestBase {

    private static RetrieveProvider rp = new RetrieveProvider() {

        @Override
        public Iterable<Object> retrieve(
                String context,
                String contextPath,
                Object contextValue,
                String dataType,
                String templateId,
                String codePath,
                Iterable<Code> codes,
                String valueSet,
                String datePath,
                String dateLowPath,
                String dateHighPath,
                Interval dateRange) {
            switch (dataType) {
                case "Encounter":
                    return singletonList(new Encounter());
                case "Condition":
                    return singletonList(new Condition());
                case "Patient":
                    return singletonList(new Patient());
                default:
                    break;
            }

            return null;
        }
    };

    @Test
    public void testWithCache() {
        CqlEngine engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, rp));
        engine.getCache().setExpressionCaching(true);
        EvaluationResult evaluationResult =
                engine.evaluate(library.getIdentifier(), Set.of("Union"), null, null, null, null);

        Object result = evaluationResult.forExpression("Union").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.forExpression("Union").evaluatedResources().size(), is(2));
        engine.getState().clearEvaluatedResources();

        evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("Encounter"), null, null, null, null);
        result = evaluationResult.forExpression("Encounter").value();
        assertThat(result, instanceOf(List.class));
        assertThat(
                evaluationResult.forExpression("Encounter").evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();

        evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("Condition"), null, null, null, null);
        result = evaluationResult.forExpression("Condition").value();
        assertThat(result, instanceOf(List.class));
        assertThat(
                evaluationResult.forExpression("Condition").evaluatedResources().size(), is(1));
    }

    @Test
    public void testWithoutCache() {
        CqlEngine engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, rp));
        EvaluationResult evaluationResult =
                engine.evaluate(library.getIdentifier(), Set.of("Union"), null, null, null, null);

        Object result = evaluationResult.forExpression("Union").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.forExpression("Union").evaluatedResources().size(), is(2));
        engine.getState().clearEvaluatedResources();

        evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("Encounter"), null, null, null, null);
        result = evaluationResult.forExpression("Encounter").value();
        assertThat(result, instanceOf(List.class));
        assertThat(
                evaluationResult.forExpression("Encounter").evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();

        evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("Condition"), null, null, null, null);
        result = evaluationResult.forExpression("Condition").value();
        assertThat(result, instanceOf(List.class));
        assertThat(
                evaluationResult.forExpression("Condition").evaluatedResources().size(), is(1));

        evaluationResult = engine.evaluate(library.getIdentifier(), Set.of("Union"), null, null, null, null);
        result = evaluationResult.forExpression("Union").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.forExpression("Union").evaluatedResources().size(), is(2));
        engine.getState().clearEvaluatedResources();
    }
}
