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
import org.hl7.fhirpath.TranslatorHelper;
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
        public Iterable<Object> retrieve(String context, String contextPath, Object contextValue, String dataType,
                String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath,
                String dateLowPath, String dateHighPath, Interval dateRange) {
            switch(dataType) {
                case "Encounter": return singletonList(new Encounter());
                case "Condition": return singletonList(new Condition());
                case "Patient": return singletonList(new Patient());
                default: break;
            }

            return null;
        }
    };

    @Test
    public void testWithCache() {
        CqlEngine engineVisitor = TranslatorHelper.getEngineVisitor();
        engineVisitor.getState().registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, rp));
        engineVisitor.getCache().setExpressionCaching(true);
        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("Union"), null, null, null, null);


        Object result = evaluationResult.expressionResults.get("Union").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.expressionResults.get("Union").evaluatedResources().size(), is(2));
        engineVisitor.getState().clearEvaluatedResources();


        evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("Encounter"), null, null, null, null);
        result = evaluationResult.expressionResults.get("Encounter").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.expressionResults.get("Encounter").evaluatedResources().size(), is(1));
        engineVisitor.getState().clearEvaluatedResources();


        evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("Condition"), null, null, null, null);
        result = evaluationResult.expressionResults.get("Condition").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.expressionResults.get("Condition").evaluatedResources().size(), is(1));

    }

    @Test
    public void testWithoutCache() {
        CqlEngine engineVisitor = TranslatorHelper.getEngineVisitor();
        engineVisitor.getState().registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, rp));
        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("Union"), null, null, null, null);

        Object result = evaluationResult.expressionResults.get("Union").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.expressionResults.get("Union").evaluatedResources().size(), is(2));
        engineVisitor.getState().clearEvaluatedResources();


        evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("Encounter"), null, null, null, null);
        result = evaluationResult.expressionResults.get("Encounter").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.expressionResults.get("Encounter").evaluatedResources().size(), is(1));
        engineVisitor.getState().clearEvaluatedResources();


        evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("Condition"), null, null, null, null);
        result = evaluationResult.expressionResults.get("Condition").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.expressionResults.get("Condition").evaluatedResources().size(), is(1));

        evaluationResult = engineVisitor.evaluate(library.getIdentifier(), getLibraryMap(),
                Set.of("Union"), null, null, null, null);
        result = evaluationResult.expressionResults.get("Union").value();
        assertThat(result, instanceOf(List.class));
        assertThat(evaluationResult.expressionResults.get("Union").evaluatedResources().size(), is(2));
        engineVisitor.getState().clearEvaluatedResources();
    }
}
