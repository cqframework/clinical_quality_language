package org.opencds.cqf.cql.engine.fhir.data;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEvaluationResult;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_ARRIVED_PAT1;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_ARRIVED_PAT2;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_ARRIVED_PAT3;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT1;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT2;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED_PAT3;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.getAllEncounters;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See EvaluatedResourcesMultiLibComplexDepsTest.md for a mermaid diagram of the library dependencies
 */
class EvaluatedResourcesMultiLibComplexDepsTest extends FhirExecutionMultiLibTestBase {

    private static final Logger log = LoggerFactory.getLogger(EvaluatedResourcesMultiLibComplexDepsTest.class);

    private static final RetrieveProvider RETRIEVE_PROVIDER_COMPLEX =
            EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.INSTANCE;

    private static final VersionedIdentifier LIB_1A =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibComplexDepsTest_Level1A");
    private static final VersionedIdentifier LIB_1B =
            EvaluatedResourceTestUtils.forId("EvaluatedResourcesMultiLibComplexDepsTest_Level1B");

    private static final String EXPRESSION_ENCOUNTERS_A = "Encounters A";
    private static final String EXPRESSION_ENCOUNTERS_B = "Encounters B";
    private static final Set<String> ALL_EXPRESSIONS = Set.of(EXPRESSION_ENCOUNTERS_A, EXPRESSION_ENCOUNTERS_B);

    private static Stream<Arguments> singleLibraryEvaluationParams() {
        return Stream.of(
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(
                                ENCOUNTER_ARRIVED_PAT1,
                                ENCOUNTER_PLANNED_PAT1,
                                ENCOUNTER_ARRIVED_PAT2,
                                ENCOUNTER_PLANNED_PAT2,
                                ENCOUNTER_ARRIVED_PAT3,
                                ENCOUNTER_PLANNED_PAT3)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(ENCOUNTER_ARRIVED_PAT1, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        getAllEncounters(),
                        List.of(ENCOUNTER_PLANNED_PAT1, ENCOUNTER_PLANNED_PAT2, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        true, LIB_1B, EXPRESSION_ENCOUNTERS_B, getAllEncounters(), List.of(ENCOUNTER_PLANNED_PAT1)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(
                                ENCOUNTER_ARRIVED_PAT1,
                                ENCOUNTER_PLANNED_PAT1,
                                ENCOUNTER_ARRIVED_PAT2,
                                ENCOUNTER_PLANNED_PAT2,
                                ENCOUNTER_ARRIVED_PAT3,
                                ENCOUNTER_PLANNED_PAT3)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(ENCOUNTER_ARRIVED_PAT1, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        getAllEncounters(),
                        List.of(ENCOUNTER_PLANNED_PAT1, ENCOUNTER_PLANNED_PAT2, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        false, LIB_1B, EXPRESSION_ENCOUNTERS_B, getAllEncounters(), List.of(ENCOUNTER_PLANNED_PAT1)));
    }

    @ParameterizedTest
    @MethodSource("singleLibraryEvaluationParams")
    void singleLibraryEvaluation(
            boolean expressionCaching,
            VersionedIdentifier libraryIdentifier,
            String expressionName,
            List<IBaseResource> expectedEvaluatedResources,
            List<IBaseResource> expectedValues) {

        var engine = getCqlEngineForFhirExistingLibMgr(expressionCaching);

        var resultsSingleLib = engine.evaluate(libraryIdentifier, ALL_EXPRESSIONS);

        assertEvaluationResult(resultsSingleLib, expressionName, expectedEvaluatedResources, expectedValues);
    }

    private static Stream<Arguments> multiLibrarySingleEvaluationAtATimeParams() {
        return Stream.of(
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(
                                ENCOUNTER_ARRIVED_PAT1,
                                ENCOUNTER_PLANNED_PAT1,
                                ENCOUNTER_ARRIVED_PAT2,
                                ENCOUNTER_PLANNED_PAT2,
                                ENCOUNTER_ARRIVED_PAT3,
                                ENCOUNTER_PLANNED_PAT3)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(ENCOUNTER_ARRIVED_PAT1, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        getAllEncounters(),
                        List.of(ENCOUNTER_PLANNED_PAT1, ENCOUNTER_PLANNED_PAT2, ENCOUNTER_PLANNED_PAT3)),
                Arguments.of(
                        true, LIB_1B, EXPRESSION_ENCOUNTERS_B, getAllEncounters(), List.of(ENCOUNTER_PLANNED_PAT1)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(
                                ENCOUNTER_ARRIVED_PAT1,
                                ENCOUNTER_PLANNED_PAT1,
                                ENCOUNTER_ARRIVED_PAT2,
                                ENCOUNTER_PLANNED_PAT2,
                                ENCOUNTER_ARRIVED_PAT3,
                                ENCOUNTER_PLANNED_PAT3)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(ENCOUNTER_ARRIVED_PAT1, ENCOUNTER_PLANNED_PAT1)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        getAllEncounters(),
                        List.of(ENCOUNTER_PLANNED_PAT1, ENCOUNTER_PLANNED_PAT2, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        false, LIB_1B, EXPRESSION_ENCOUNTERS_B, getAllEncounters(), List.of(ENCOUNTER_PLANNED_PAT1)));
    }

    @ParameterizedTest
    @MethodSource("multiLibrarySingleEvaluationAtATimeParams")
    void multiLibrarySingleEvaluationAtATime(
            boolean expressionCaching,
            VersionedIdentifier libraryIdentifier,
            String expressionName,
            List<IBaseResource> expectedEvaluatedResources,
            List<IBaseResource> expectedValues) {

        var engine = getCqlEngineForFhirExistingLibMgr(expressionCaching);

        var resultsSingleLib = engine.evaluate(List.of(libraryIdentifier), ALL_EXPRESSIONS);

        assertTrue(resultsSingleLib.containsResultsFor(libraryIdentifier));
        assertTrue(resultsSingleLib.containsResultsFor(libraryIdentifier));
        assertFalse(resultsSingleLib.containsExceptionsFor(libraryIdentifier));
        assertFalse(resultsSingleLib.containsExceptionsFor(libraryIdentifier));
        assertNotNull(resultsSingleLib.getResultFor(libraryIdentifier));
        assertNotNull(resultsSingleLib.getOnlyResultOrThrow());
        assertNull(resultsSingleLib.getExceptionFor(libraryIdentifier));

        assertEvaluationResult(
                resultsSingleLib, libraryIdentifier, expressionName, expectedEvaluatedResources, expectedValues);
    }

    private static Stream<Arguments> multiLibraryEvaluationParams() {
        return Stream.of(
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(
                                ENCOUNTER_ARRIVED_PAT1,
                                ENCOUNTER_PLANNED_PAT1,
                                ENCOUNTER_ARRIVED_PAT2,
                                ENCOUNTER_PLANNED_PAT2,
                                ENCOUNTER_ARRIVED_PAT3,
                                ENCOUNTER_PLANNED_PAT3)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(ENCOUNTER_ARRIVED_PAT1, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        getAllEncounters(),
                        List.of(ENCOUNTER_PLANNED_PAT1, ENCOUNTER_PLANNED_PAT2, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        true, LIB_1B, EXPRESSION_ENCOUNTERS_B, getAllEncounters(), List.of(ENCOUNTER_PLANNED_PAT1)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(
                                ENCOUNTER_ARRIVED_PAT1,
                                ENCOUNTER_PLANNED_PAT1,
                                ENCOUNTER_ARRIVED_PAT2,
                                ENCOUNTER_PLANNED_PAT2,
                                ENCOUNTER_ARRIVED_PAT3,
                                ENCOUNTER_PLANNED_PAT3)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        getAllEncounters(),
                        List.of(ENCOUNTER_ARRIVED_PAT1, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        getAllEncounters(),
                        List.of(ENCOUNTER_PLANNED_PAT1, ENCOUNTER_PLANNED_PAT2, ENCOUNTER_PLANNED_PAT2)),
                Arguments.of(
                        false, LIB_1B, EXPRESSION_ENCOUNTERS_B, getAllEncounters(), List.of(ENCOUNTER_PLANNED_PAT1)));
    }

    @ParameterizedTest
    @MethodSource("multiLibraryEvaluationParams")
    void multiLibraryEvaluation(
            boolean expressionCaching,
            VersionedIdentifier libraryIdentifier,
            String expressionName,
            List<IBaseResource> expectedEvaluatedResources,
            List<IBaseResource> expectedValues) {

        var engine = getCqlEngineForFhirExistingLibMgr(expressionCaching);

        var allLibs = List.of(LIB_1A, LIB_1B);

        var results = engine.evaluate(allLibs, ALL_EXPRESSIONS);

        assertTrue(results.containsResultsFor(LIB_1A));
        assertTrue(results.containsResultsFor(LIB_1B));
        assertFalse(results.containsExceptionsFor(LIB_1A));
        assertFalse(results.containsExceptionsFor(LIB_1B));
        assertNotNull(results.getResultFor(LIB_1A));
        assertThrows(IllegalStateException.class, results::getOnlyResultOrThrow);
        assertNull(results.getExceptionFor(LIB_1A));

        assertEvaluationResult(results, libraryIdentifier, expressionName, expectedEvaluatedResources, expectedValues);
    }

    @Nonnull
    private CqlEngine getCqlEngineForFhirExistingLibMgr(boolean expressionCaching) {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
                getEngineWithExistingLibraryManager(), expressionCaching, r4ModelResolver, RETRIEVE_PROVIDER_COMPLEX);
    }

    @Nonnull
    private CqlEngine getCqlEngineForFhirNewLibMgr(boolean expressionCaching) {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
                getEngineWithNewLibraryManager(), expressionCaching, r4ModelResolver, RETRIEVE_PROVIDER_COMPLEX);
    }
}
