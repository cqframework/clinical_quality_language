package org.opencds.cqf.cql.engine.fhir.data;

import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEvaluationResult;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_ARRIVED;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_CANCELLED;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_FINISHED;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_TRIAGED;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.SearchableLibraryIdentifier;
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

    private static final SearchableLibraryIdentifier LIB_1A =
            SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibComplexDepsTest_Level1A");
    private static final SearchableLibraryIdentifier LIB_1B =
            SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibComplexDepsTest_Level1B");
    private static final SearchableLibraryIdentifier LIB_2 =
            SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibComplexDepsTest_Level2");
    private static final SearchableLibraryIdentifier LIB_3A =
            SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibComplexDepsTest_Level3A");
    private static final SearchableLibraryIdentifier LIB_3B =
            SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibComplexDepsTest_Level3A");
    private static final SearchableLibraryIdentifier LIB_4 =
            SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibComplexDepsTest_Level4");
    private static final SearchableLibraryIdentifier LIB_5 =
            SearchableLibraryIdentifier.fromId("EvaluatedResourcesMultiLibComplexDepsTest_Level5");

    private static final String EXPRESSION_ENCOUNTERS_A = "Encounters A";
    private static final String EXPRESSION_ENCOUNTERS_B = "Encounters B";
    private static final Set<String> ALL_EXPRESSIONS = Set.of(EXPRESSION_ENCOUNTERS_A, EXPRESSION_ENCOUNTERS_B);

    private static Stream<Arguments> singleLibraryEvaluationParams() {
        return Stream.of(
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_PLANNED)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_TRIAGED)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_PLANNED)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_TRIAGED)));
    }

    @ParameterizedTest
    @MethodSource("singleLibraryEvaluationParams")
    void singleLibraryEvaluation(
            boolean expressionCaching,
            SearchableLibraryIdentifier libraryIdentifier,
            String expressionName,
            List<IBaseResource> expectedEvaluatedResources,
            List<IBaseResource> expectedValues) {
        var engine = getCqlEngineForFhirNewLibMgr(expressionCaching);

        var resultsSingleLib = engine.evaluate(libraryIdentifier.toIdentifier(), ALL_EXPRESSIONS);

        assertEvaluationResult(resultsSingleLib, expressionName, expectedEvaluatedResources, expectedValues);
    }

    private static Stream<Arguments> multiLibrarySingleEvaluationAtATimeParams() {
        return Stream.of(
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_PLANNED)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_TRIAGED)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_PLANNED)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_TRIAGED)));
    }

    @ParameterizedTest
    @MethodSource("multiLibrarySingleEvaluationAtATimeParams")
    void multiLibrarySingleEvaluationAtATime(
            boolean expressionCaching,
            SearchableLibraryIdentifier libraryIdentifier,
            String expressionName,
            List<IBaseResource> expectedEvaluatedResources,
            List<IBaseResource> expectedValues) {

        var engine = getCqlEngineForFhirNewLibMgr(expressionCaching);

        var resultsSingleLib = engine.evaluate(List.of(libraryIdentifier.toIdentifier()), ALL_EXPRESSIONS);

        assertEvaluationResult(
                resultsSingleLib, libraryIdentifier, expressionName, expectedEvaluatedResources, expectedValues);
    }

    private static Stream<Arguments> multiLibraryEvaluationParams() {
        return Stream.of(
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        true,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_PLANNED)),
                Arguments.of(
                        true,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_TRIAGED)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_A,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)),
                Arguments.of(
                        false,
                        LIB_1A,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_PLANNED)),
                Arguments.of(
                        false,
                        LIB_1B,
                        EXPRESSION_ENCOUNTERS_B,
                        List.of(
                                ENCOUNTER_ARRIVED,
                                ENCOUNTER_CANCELLED,
                                ENCOUNTER_FINISHED,
                                ENCOUNTER_PLANNED,
                                ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_TRIAGED)));
    }

    @ParameterizedTest
    @MethodSource("multiLibraryEvaluationParams")
    void multiLibraryEvaluation(
            boolean expressionCaching,
            SearchableLibraryIdentifier libraryIdentifier,
            String expressionName,
            List<IBaseResource> expectedEvaluatedResources,
            List<IBaseResource> expectedValues) {
        var engine = getCqlEngineForFhirNewLibMgr(expressionCaching);

        var allLibs = List.of(LIB_1A.toIdentifier(), LIB_1B.toIdentifier());

        var resultsSingleLib = engine.evaluate(allLibs, ALL_EXPRESSIONS);

        log.info("resultsSingleLib: {}", printEvaluationResult(resultsSingleLib));

        assertEvaluationResult(
                resultsSingleLib, libraryIdentifier, expressionName, expectedEvaluatedResources, expectedValues);
    }

    @Nonnull
    private CqlEngine getCqlEngineForFhirNewLibMgr(boolean expressionCaching) {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
                getEngineWithNewLibraryManager(), expressionCaching, r4ModelResolver, RETRIEVE_PROVIDER_COMPLEX);
    }
}
