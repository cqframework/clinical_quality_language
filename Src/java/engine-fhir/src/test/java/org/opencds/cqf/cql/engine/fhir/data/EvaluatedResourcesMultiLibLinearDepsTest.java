package org.opencds.cqf.cql.engine.fhir.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.CONDITION;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.ENCOUNTER;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.PROCEDURE;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.RETRIEVE_PROVIDER;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEntireEvaluationResult;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEvaluationResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.cqframework.cql.cql2elm.CqlIncludeException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.execution.CqlEngine;

class EvaluatedResourcesMultiLibLinearDepsTest extends FhirExecutionMultiLibTestBase {

    private static final VersionedIdentifier LIB_1 =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibLinearDepsTest1");
    private static final VersionedIdentifier LIB_2 =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibLinearDepsTest2");
    private static final VersionedIdentifier LIB_3 =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibLinearDepsTest3");
    private static final List<VersionedIdentifier> ALL_LIB_IDS = List.of(LIB_1, LIB_2, LIB_3);

    private static final String UNION_EXPRESSION = "Union";
    private static final String ENCOUNTER_EXPRESSION = ResourceType.Encounter.name();
    private static final String CONDITION_EXPRESSION = ResourceType.Condition.name();

    private static final Set<String> ALL_EXPRESSIONS =
            Set.of(UNION_EXPRESSION, ENCOUNTER_EXPRESSION, CONDITION_EXPRESSION);

    private static Stream<Arguments> singleLibParams() {
        return Stream.of(
                Arguments.of(
                        LIB_1, UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER), List.of(CONDITION, ENCOUNTER), true),
                Arguments.of(LIB_1, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(ENCOUNTER), true),
                Arguments.of(LIB_1, CONDITION_EXPRESSION, List.of(CONDITION), List.of(CONDITION), true),
                Arguments.of(
                        LIB_1, UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER), List.of(CONDITION, ENCOUNTER), false),
                Arguments.of(LIB_1, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(ENCOUNTER), false),
                Arguments.of(LIB_1, CONDITION_EXPRESSION, List.of(CONDITION), List.of(CONDITION), false),
                Arguments.of(
                        LIB_2, UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER), List.of(CONDITION, ENCOUNTER), true),
                Arguments.of(LIB_2, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(), true),
                Arguments.of(LIB_2, CONDITION_EXPRESSION, List.of(CONDITION), List.of(CONDITION), true),
                Arguments.of(
                        LIB_2, UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER), List.of(CONDITION, ENCOUNTER), false),
                Arguments.of(LIB_2, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(), false),
                Arguments.of(LIB_2, CONDITION_EXPRESSION, List.of(CONDITION), List.of(CONDITION), false),
                Arguments.of(
                        LIB_3, UNION_EXPRESSION, List.of(ENCOUNTER, PROCEDURE), List.of(ENCOUNTER, PROCEDURE), true),
                Arguments.of(LIB_3, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(ENCOUNTER), true),
                Arguments.of(LIB_3, CONDITION_EXPRESSION, List.of(CONDITION), List.of(), true),
                Arguments.of(
                        LIB_3, UNION_EXPRESSION, List.of(ENCOUNTER, PROCEDURE), List.of(ENCOUNTER, PROCEDURE), false),
                Arguments.of(LIB_3, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(ENCOUNTER), false),
                Arguments.of(LIB_3, CONDITION_EXPRESSION, List.of(CONDITION), List.of(), false));
    }

    @ParameterizedTest
    @MethodSource("singleLibParams")
    void singleLib(
            VersionedIdentifier libId,
            String expressionName,
            List<? extends IBaseResource> expectedResources,
            List<? extends IBaseResource> expectedValues,
            boolean expressionCaching) {
        var engine = getCqlEngineForFhirNewLibMgr(expressionCaching);

        // Old single-lib API
        var singleResult = engine.evaluate(libId, Set.of(expressionName));

        assertEvaluationResult(singleResult, expressionName, expectedResources, expectedValues);

        // Old multi-lib API passing a single lib
        var multiResult =
                engine.evaluate(List.of(libId), Set.of(expressionName)).getOnlyResultOrThrow();

        assertEvaluationResult(multiResult, expressionName, expectedResources, expectedValues);
    }

    @Test
    void singleLibNonexistentLib() {
        var engine = getCqlEngineForFhirNewLibMgr(false);

        // Old single-lib API
        var singleLibException = assertThrows(
                CqlIncludeException.class, () -> engine.evaluate(new VersionedIdentifier().withId("bad"), Set.of()));

        assertThat(singleLibException.getMessage(), startsWith("Could not load source for library bad"));

        // Old multi-lib API passing a single lib
        var multiLibException = assertThrows(
                CqlIncludeException.class,
                () -> engine.evaluate(List.of(new VersionedIdentifier().withId("bad")), Set.of()));

        assertThat(multiLibException.getMessage(), startsWith("Could not load source for library bad"));
    }

    private static Stream<Arguments> multiLibParams() {
        return Stream.of(
                Arguments.of(
                        LIB_1, UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER), List.of(CONDITION, ENCOUNTER), true),
                Arguments.of(LIB_1, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(ENCOUNTER), true),
                Arguments.of(LIB_1, CONDITION_EXPRESSION, List.of(CONDITION), List.of(CONDITION), true),
                Arguments.of(
                        LIB_1, UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER), List.of(CONDITION, ENCOUNTER), false),
                Arguments.of(LIB_1, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(ENCOUNTER), false),
                Arguments.of(LIB_1, CONDITION_EXPRESSION, List.of(CONDITION), List.of(CONDITION), false),
                Arguments.of(
                        LIB_2, UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER), List.of(CONDITION, ENCOUNTER), true),
                Arguments.of(LIB_2, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(), true),
                Arguments.of(LIB_2, CONDITION_EXPRESSION, List.of(CONDITION), List.of(CONDITION), true),
                Arguments.of(
                        LIB_2, UNION_EXPRESSION, List.of(CONDITION, ENCOUNTER), List.of(CONDITION, ENCOUNTER), false),
                Arguments.of(LIB_2, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(), false),
                Arguments.of(LIB_2, CONDITION_EXPRESSION, List.of(CONDITION), List.of(CONDITION), false),
                Arguments.of(
                        LIB_3, UNION_EXPRESSION, List.of(ENCOUNTER, PROCEDURE), List.of(ENCOUNTER, PROCEDURE), true),
                Arguments.of(LIB_3, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(ENCOUNTER), true),
                Arguments.of(LIB_3, CONDITION_EXPRESSION, List.of(CONDITION), List.of(), true),
                Arguments.of(
                        LIB_3, UNION_EXPRESSION, List.of(ENCOUNTER, PROCEDURE), List.of(ENCOUNTER, PROCEDURE), false),
                Arguments.of(LIB_3, ENCOUNTER_EXPRESSION, List.of(ENCOUNTER), List.of(ENCOUNTER), false),
                Arguments.of(LIB_3, CONDITION_EXPRESSION, List.of(CONDITION), List.of(), false));
    }

    // Note that there's no obvious way to set up a scenario in which a multilib evaluation will result in different
    // evaluated resources among the libraries in the same evaluation, but this test can and will assert different
    // values
    // for each library.
    @ParameterizedTest
    @MethodSource("multiLibParams")
    void multiLib(
            VersionedIdentifier libId,
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
        return Stream.of(Arguments.of(true), Arguments.of(false));
    }

    @ParameterizedTest
    @MethodSource("multiLibEnsurePartialCacheAllowsUncachedLibsToBeCompiledParams")
    void multiLibEnsurePartialCacheAllowsUncachedLibsToBeCompiled(boolean expressionCaching) {
        var engine = getCqlEngineForFhirNewLibMgr(expressionCaching);

        // Compile only one library:  it will be cached
        var resultsSingleLib = engine.evaluate(List.of(LIB_1), ALL_EXPRESSIONS);

        assertEntireEvaluationResult(
                resultsSingleLib,
                LIB_1,
                Map.of(
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER)),
                Map.of(
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of(CONDITION)));
        engine.getState().clearEvaluatedResources();

        // Using the same engine, evaluate three libraries, two of which are not cached
        var resultsMultiLib = engine.evaluate(ALL_LIB_IDS, ALL_EXPRESSIONS);

        assertEntireEvaluationResult(
                resultsMultiLib,
                LIB_1,
                Map.of(
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER)),
                Map.of(
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of(CONDITION)));

        assertEntireEvaluationResult(
                resultsMultiLib,
                LIB_2,
                Map.of(
                        UNION_EXPRESSION,
                        List.of(PROCEDURE, CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION,
                        List.of(PROCEDURE, CONDITION, ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of(PROCEDURE, CONDITION, ENCOUNTER)),
                Map.of(
                        UNION_EXPRESSION,
                        List.of(PROCEDURE, CONDITION),
                        ENCOUNTER_EXPRESSION,
                        List.of(),
                        CONDITION_EXPRESSION,
                        List.of(CONDITION)));

        assertEntireEvaluationResult(
                resultsMultiLib,
                LIB_3,
                Map.of(
                        UNION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE, CONDITION),
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE, CONDITION),
                        CONDITION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE, CONDITION)),
                Map.of(
                        UNION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE),
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of()));

        // Now use the same engine, but pass the identifiers in a different order
        var resultsMultiLibDifferentOrder =
                engine.evaluate(Stream.of(LIB_3, LIB_2, LIB_1).toList(), ALL_EXPRESSIONS);

        assertEntireEvaluationResult(
                resultsMultiLibDifferentOrder,
                LIB_1,
                Map.of(
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER)),
                Map.of(
                        UNION_EXPRESSION,
                        List.of(CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of(CONDITION)));

        assertEntireEvaluationResult(
                resultsMultiLibDifferentOrder,
                LIB_2,
                Map.of(
                        UNION_EXPRESSION,
                        List.of(PROCEDURE, CONDITION, ENCOUNTER),
                        ENCOUNTER_EXPRESSION,
                        List.of(PROCEDURE, CONDITION, ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of(PROCEDURE, CONDITION, ENCOUNTER)),
                Map.of(
                        UNION_EXPRESSION,
                        List.of(PROCEDURE, CONDITION),
                        ENCOUNTER_EXPRESSION,
                        List.of(),
                        CONDITION_EXPRESSION,
                        List.of(CONDITION)));

        assertEntireEvaluationResult(
                resultsMultiLibDifferentOrder,
                LIB_3,
                Map.of(
                        UNION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE, CONDITION),
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE, CONDITION),
                        CONDITION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE, CONDITION)),
                Map.of(
                        UNION_EXPRESSION,
                        List.of(ENCOUNTER, PROCEDURE),
                        ENCOUNTER_EXPRESSION,
                        List.of(ENCOUNTER),
                        CONDITION_EXPRESSION,
                        List.of()));
    }

    @Nonnull
    private CqlEngine getCqlEngineForFhirNewLibMgr(boolean expressionCaching) {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
                getEngineWithNewLibraryManager(), expressionCaching, r4ModelResolver, RETRIEVE_PROVIDER);
    }
}
