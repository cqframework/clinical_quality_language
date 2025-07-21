package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.SearchableLibraryIdentifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.CONDITION;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.ENCOUNTER;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.RETRIEVE_PROVIDER;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEntireEvaluationResult;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEvaluationResult;

class EvaluatedResourcesMultiLibTest extends FhirExecutionMultiLibTestBase {

    private static Stream<Arguments> singleLibParams() {
        return Stream.of(
                Arguments.of("Union", List.of(CONDITION, ENCOUNTER), true),
                Arguments.of("Encounter", List.of(ENCOUNTER), true),
                Arguments.of("Condition", List.of(CONDITION), true),
                Arguments.of("Union", List.of(CONDITION, ENCOUNTER), false),
                Arguments.of("Encounter", List.of(ENCOUNTER), false),
                Arguments.of("Condition", List.of(CONDITION), false)
        );
    }

    @ParameterizedTest
    @MethodSource("singleLibParams")
    void singleLib(String expressionName, List<? extends IBaseResource> expectedResources, boolean expressionCaching) {
        var engine = getCqlEngineForFhir(expressionCaching);

        var results =
                engine.evaluate(
                        getFirstLibraryIdentifierAsList(),
                        Set.of("Union", "Encounter", "Condition"));

        var resultFirst = results.getResults().entrySet().iterator().next().getValue();

        assertEntireEvaluationResult(
                resultFirst,
                Map.of("Union", List.of(CONDITION, ENCOUNTER),
                        "Encounter", List.of(CONDITION, ENCOUNTER),
                        "Condition", List.of(CONDITION, ENCOUNTER)),
                Map.of("Union", List.of(CONDITION, ENCOUNTER),
                        "Encounter", List.of(ENCOUNTER),
                        "Condition", List.of(CONDITION)));


        results = engine.evaluate(getFirstLibraryIdentifierAsList(), Set.of(expressionName));
        resultFirst = results.getResults().entrySet().iterator().next().getValue();
        assertEvaluationResult(resultFirst, expressionName, expectedResources);
    }


    private static Stream<Arguments> multiLibParams() {
        return Stream.of(
                Arguments.of(true),
                Arguments.of(false)
        );
    }

    @ParameterizedTest
    @MethodSource("multiLibParams")
    void multiLib(boolean expressionCaching) {
        var engine = getCqlEngineForFhir(expressionCaching);

        var results = engine.evaluate(getAllLibraryIdentifiers(), Set.of("Union"));

        var result1 = results.getResults().get(SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibTest1"));
        assertEvaluationResult(result1, "Union", List.of(CONDITION, ENCOUNTER));
        engine.getState().clearEvaluatedResources();

        results = engine.evaluate(getFirstLibraryIdentifierAsList(), Set.of("Encounter"));
        result1 = results.getResults().entrySet().iterator().next().getValue();

        assertEvaluationResult(result1, "Encounter", List.of(ENCOUNTER));
        engine.getState().clearEvaluatedResources();

        results = engine.evaluate(getFirstLibraryIdentifierAsList(), Set.of("Condition"));
        result1 = results.getResults().entrySet().iterator().next().getValue();

        assertEvaluationResult(result1, "Condition", List.of(CONDITION));

        results = engine.evaluate(getFirstLibraryIdentifierAsList(), Set.of("Union"));
        result1 = results.getResults().entrySet().iterator().next().getValue();

        assertEvaluationResult(result1, "Union", List.of(ENCOUNTER, CONDITION));
        engine.getState().clearEvaluatedResources();

        var result2 = results.getResults().get(SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibTest2"));
        var result3 = results.getResults().get(SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibTest3"));
    }


    @Nonnull
    private CqlEngine getCqlEngineForFhir(boolean expressionCaching) {
        CqlEngine engine = getEngine();
        engine.getState()
                .getEnvironment()
                .registerDataProvider("http://hl7.org/fhir", new CompositeDataProvider(r4ModelResolver, RETRIEVE_PROVIDER));
        engine.getCache().setExpressionCaching(expressionCaching);
        return engine;
    }
}
