package org.opencds.cqf.cql.engine.fhir.data;

import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.CONDITION;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.ENCOUNTER;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.RETRIEVE_PROVIDER;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEvaluationResult;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;

class EvaluatedResourcesTest extends FhirExecutionTestBase {

    @Test
    void withCache() {
        CqlEngine engine = getCqlEngineForFhir(true);

        var results = engine.evaluate(library.getIdentifier(), Set.of("Union"));

        assertEvaluationResult(results, "Union", List.of(CONDITION, ENCOUNTER));

        results = engine.evaluate(library.getIdentifier(), Set.of("Encounter"));
        assertEvaluationResult(results, "Encounter", List.of(ENCOUNTER));

        results = engine.evaluate(library.getIdentifier(), Set.of("Condition"));
        assertEvaluationResult(results, "Condition", List.of(CONDITION));
    }

    @Test
    void withoutCache() {
        CqlEngine engine = getCqlEngineForFhir(false);

        var results = engine.evaluate(library.getIdentifier(), Set.of("Union"));

        assertEvaluationResult(results, "Union", List.of(CONDITION, ENCOUNTER));

        results = engine.evaluate(library.getIdentifier(), Set.of("Encounter"));
        assertEvaluationResult(results, "Encounter", List.of(ENCOUNTER));

        results = engine.evaluate(library.getIdentifier(), Set.of("Condition"));
        assertEvaluationResult(results, "Condition", List.of(CONDITION));

        results = engine.evaluate(library.getIdentifier(), Set.of("Union"));
        assertEvaluationResult(results, "Union", List.of(CONDITION, ENCOUNTER));
    }

    @Nonnull
    private CqlEngine getCqlEngineForFhir(boolean expressionCaching) {
        CqlEngine engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider(
                        "http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, RETRIEVE_PROVIDER));
        engine.getCache().setExpressionCaching(expressionCaching);
        return engine;
    }
}
