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
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;

class EvaluatedResourcesTest extends FhirExecutionTestBase {

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
    void withCache() {
        CqlEngine engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, rp));
        engine.getCache().setExpressionCaching(true);
        var results = engine.evaluate(library.getIdentifier(), Set.of("Union"));

        Object value = results.forExpression("Union").value();
        assertThat(value, instanceOf(List.class));
        assertThat(results.forExpression("Union").evaluatedResources().size(), is(2));
        engine.getState().clearEvaluatedResources();

        results = engine.evaluate(library.getIdentifier(), Set.of("Encounter"));
        value = results.forExpression("Encounter").value();
        assertThat(value, instanceOf(List.class));
        assertThat(results.forExpression("Encounter").evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();

        results = engine.evaluate(library.getIdentifier(), Set.of("Condition"));
        value = results.forExpression("Condition").value();
        assertThat(value, instanceOf(List.class));
        assertThat(results.forExpression("Condition").evaluatedResources().size(), is(1));
    }

    @Test
    void withoutCache() {
        CqlEngine engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, rp));
        var results = engine.evaluate(library.getIdentifier(), Set.of("Union"));

        Object value = results.forExpression("Union").value();
        assertThat(value, instanceOf(List.class));
        assertThat(results.forExpression("Union").evaluatedResources().size(), is(2));
        engine.getState().clearEvaluatedResources();

        results = engine.evaluate(library.getIdentifier(), Set.of("Encounter"));
        value = results.forExpression("Encounter").value();
        assertThat(value, instanceOf(List.class));
        assertThat(results.forExpression("Encounter").evaluatedResources().size(), is(1));
        engine.getState().clearEvaluatedResources();

        results = engine.evaluate(library.getIdentifier(), Set.of("Condition"));
        value = results.forExpression("Condition").value();
        assertThat(value, instanceOf(List.class));
        assertThat(results.forExpression("Condition").evaluatedResources().size(), is(1));

        results = engine.evaluate(library.getIdentifier(), Set.of("Union"));
        value = results.forExpression("Union").value();
        assertThat(value, instanceOf(List.class));
        assertThat(results.forExpression("Union").evaluatedResources().size(), is(2));
        engine.getState().clearEvaluatedResources();
    }
}
