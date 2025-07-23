package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.SearchableLibraryIdentifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.CONDITION;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.ENCOUNTER;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.PROCEDURE;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.RETRIEVE_PROVIDER;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEntireEvaluationResult;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEvaluationResult;

class EvaluatedResourcesMultiLibLinearDepsTest extends FhirExecutionMultiLibTestBase {

    private static final SearchableLibraryIdentifier LIB_1 = SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibLinearDepsTest1");
    private static final SearchableLibraryIdentifier LIB_2 = SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibLinearDepsTest2");
    private static final SearchableLibraryIdentifier LIB_3 = SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibLinearDepsTest3");

    private static final String UNION_EXPRESSION = "Union";
    private static final String ENCOUNTER_EXPRESSION = ResourceType.Encounter.name();
    private static final String CONDITION_EXPRESSION = ResourceType.Condition.name();

    private static final Set<String> ALL_EXPRESSIONS = Set.of(UNION_EXPRESSION, ENCOUNTER_EXPRESSION, CONDITION_EXPRESSION);

    private static Stream<Arguments> singleLibParams() {
        return Stream.of(
                Arguments.of(
                        LIB_1,
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        List.of(CONDITION, ENCOUNTER),
                        true),
                Arguments.of(
                        LIB_1,
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        List.of(ENCOUNTER),
                        true),
                Arguments.of(
                        LIB_1,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(CONDITION),
                        true),
                Arguments.of(
                        LIB_1,
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        List.of(CONDITION, ENCOUNTER),
                        false),
                Arguments.of(
                        LIB_1,
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        List.of(ENCOUNTER),
                        false),
                Arguments.of(
                        LIB_1,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(CONDITION),
                        false),
                Arguments.of(
                        LIB_2,
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        List.of(CONDITION, ENCOUNTER),
                        true),
                Arguments.of(
                        LIB_2,
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        List.of(),
                        true),
                Arguments.of(
                        LIB_2,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(CONDITION),
                        true),
                Arguments.of(
                        LIB_2,
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        List.of(CONDITION, ENCOUNTER),
                        false),
                Arguments.of(
                        LIB_2,
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        List.of(),
                        false),
                Arguments.of(
                        LIB_2,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(CONDITION),
                        false),
                Arguments.of(
                        LIB_3,
                        UNION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE),
                        List.of(ENCOUNTER, PROCEDURE),
                        true),
                Arguments.of(
                        LIB_3,
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        List.of(ENCOUNTER),
                        true),
                Arguments.of(
                        LIB_3,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(),
                        true),
                Arguments.of(
                        LIB_3,
                        UNION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE),
                        List.of(ENCOUNTER, PROCEDURE),
                        false),
                Arguments.of(
                        LIB_3,
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        List.of(ENCOUNTER),
                        false),
                Arguments.of(
                        LIB_3,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(),
                        false)
        );
    }

    @ParameterizedTest
    @MethodSource("singleLibParams")
    void singleLib(
            SearchableLibraryIdentifier libId,
            String expressionName,
            List<? extends IBaseResource> expectedResources,
            List<? extends IBaseResource> expectedValues,
            boolean expressionCaching) {
        var engine = getCqlEngineForFhirNewLibMgr(expressionCaching);

        // Old single-lib API
        var singleResult =
                engine.evaluate(libId.toIdentifier(), Set.of(expressionName));

        assertEvaluationResult(singleResult, expressionName, expectedResources, expectedValues);

        // Old multi-lib API passing a single lib
        var multiResult =
                engine.evaluate(List.of(libId.toIdentifier()), Set.of(expressionName))
                        .getFirstResult();

        assertEvaluationResult(multiResult, expressionName, expectedResources, expectedValues);
    }

    private static Stream<Arguments> multiLibParams() {
        return Stream.of(
                Arguments.of(
                        LIB_1,
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        List.of(CONDITION, ENCOUNTER),
                        true),
                Arguments.of(
                        LIB_1,
                        ENCOUNTER_EXPRESSION,
                        List.of( ENCOUNTER),
                        List.of( ENCOUNTER),
                        true),
                Arguments.of(
                        LIB_1,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(CONDITION),
                        true),
                Arguments.of(
                        LIB_1,
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        List.of(CONDITION, ENCOUNTER),
                        false),
                Arguments.of(
                        LIB_1,
                        ENCOUNTER_EXPRESSION,
                        List.of( ENCOUNTER),
                        List.of( ENCOUNTER),
                        false),
                Arguments.of(
                        LIB_1,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(CONDITION),
                        false),
                Arguments.of(
                        LIB_2,
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        List.of(CONDITION, ENCOUNTER),
                        true),
                Arguments.of(
                        LIB_2,
                        ENCOUNTER_EXPRESSION,
                        List.of( ENCOUNTER),
                        List.of( ),
                        true),
                Arguments.of(
                        LIB_2,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(CONDITION),
                        true),
                Arguments.of(
                        LIB_2,
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        List.of(CONDITION, ENCOUNTER),
                        false),
                Arguments.of(
                        LIB_2,
                        ENCOUNTER_EXPRESSION,
                        List.of( ENCOUNTER),
                        List.of( ),
                        false),
                Arguments.of(
                        LIB_2,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(CONDITION),
                        false),
                Arguments.of(
                        LIB_3,
                        UNION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE),
                        List.of(ENCOUNTER, PROCEDURE),
                        true),
                Arguments.of(
                        LIB_3,
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        List.of(ENCOUNTER),
                        true),
                Arguments.of(
                        LIB_3,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(),
                        true),
                Arguments.of(
                        LIB_3,
                        UNION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE),
                        List.of(ENCOUNTER, PROCEDURE),
                        false),
                Arguments.of(
                        LIB_3,
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        List.of(ENCOUNTER),
                        false),
                Arguments.of(
                        LIB_3,
                        CONDITION_EXPRESSION,
                        List.of(CONDITION),
                        List.of(),
                        false)
        );
    }

    // Note that there's no obvious way to set up a scenario in which a multilib evaluation will result in different
    // evaluated resources among the libraries in the same evaluation, but this test can and will assert different values
    // for each library.
    @ParameterizedTest
    @MethodSource("multiLibParams")
    void multiLib(
            SearchableLibraryIdentifier libId,
            String expressionName,
            List<? extends IBaseResource> expectedResources,
            List<? extends IBaseResource> expectedValues,
            boolean expressionCaching) {

        var engine = getCqlEngineForFhirNewLibMgr(expressionCaching);

        var results = engine.evaluate(getAllLibraryIdentifiers(), Set.of(expressionName));

        var evaluationResultForIdentifier = results.getResultFor(libId);

        assertEvaluationResult(evaluationResultForIdentifier, expressionName, expectedResources, expectedValues);
    }

    private static Stream<Arguments> multiLibEnsurePartialCacheAllowsUncachedLibsToBeCompiledParams() {
        return Stream.of(
                Arguments.of(true),
                Arguments.of(false)
        );
    }

    @ParameterizedTest
    @MethodSource("multiLibEnsurePartialCacheAllowsUncachedLibsToBeCompiledParams")
    void multiLibEnsurePartialCacheAllowsUncachedLibsToBeCompiled(boolean expressionCaching) {
        var engine = getCqlEngineForFhirNewLibMgr(expressionCaching);

        // Compile only one library:  it will be cached
        var resultsSingleLib =
                engine.evaluate( getFirstLibraryIdentifierAsList(), ALL_EXPRESSIONS);

        assertEntireEvaluationResult(
                resultsSingleLib.getResults().get(LIB_1),
                Map.of(UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION, List.of(CONDITION, ENCOUNTER),
                        CONDITION_EXPRESSION, List.of(CONDITION, ENCOUNTER)),
                Map.of(UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION, List.of(ENCOUNTER),
                        CONDITION_EXPRESSION, List.of(CONDITION)));
        engine.getState().clearEvaluatedResources();

        // Using the same engine, evaluate three libraries, two of which are not cached
        var resultsMultiLib =
                engine.evaluate(
                        getAllLibraryIdentifiers(),
                        ALL_EXPRESSIONS);

        var evaluatedResourcesMultiLibTest1 = resultsMultiLib.getResults().get(LIB_1);
        assertEntireEvaluationResult(
                evaluatedResourcesMultiLibTest1,
                Map.of(UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION, List.of(CONDITION, ENCOUNTER),
                        CONDITION_EXPRESSION, List.of(CONDITION, ENCOUNTER)),
                Map.of(UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION, List.of(ENCOUNTER),
                        CONDITION_EXPRESSION, List.of(CONDITION)));

        var evaluatedResourcesMultiLibTest2 = resultsMultiLib.getResults().get(LIB_2);
        assertEntireEvaluationResult(
                evaluatedResourcesMultiLibTest2,
                Map.of(UNION_EXPRESSION, List.of(PROCEDURE, CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION, List.of(PROCEDURE, CONDITION, ENCOUNTER),
                        CONDITION_EXPRESSION, List.of(PROCEDURE, CONDITION, ENCOUNTER)),
                Map.of(UNION_EXPRESSION, List.of(PROCEDURE, CONDITION),
                        ENCOUNTER_EXPRESSION, List.of(),
                        CONDITION_EXPRESSION, List.of(CONDITION)));

        var evaluatedResourcesMultiLibTest3 = resultsMultiLib.getResults().get(LIB_3);
        assertEntireEvaluationResult(
                evaluatedResourcesMultiLibTest3,
                Map.of(UNION_EXPRESSION, List.of(ENCOUNTER, PROCEDURE, CONDITION),
                        ENCOUNTER_EXPRESSION, List.of(ENCOUNTER, PROCEDURE, CONDITION),
                        CONDITION_EXPRESSION, List.of(ENCOUNTER, PROCEDURE, CONDITION)),
                Map.of(UNION_EXPRESSION, List.of(ENCOUNTER, PROCEDURE),
                        ENCOUNTER_EXPRESSION, List.of(ENCOUNTER),
                        CONDITION_EXPRESSION, List.of()));
    }

    @Nonnull
    private CqlEngine getCqlEngineForFhirNewLibMgr(boolean expressionCaching) {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
                getEngineWithNewLibraryManager(),
                expressionCaching,
                r4ModelResolver,
                RETRIEVE_PROVIDER);
    }
}
