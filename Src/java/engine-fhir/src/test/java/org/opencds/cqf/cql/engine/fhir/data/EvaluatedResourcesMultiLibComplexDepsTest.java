package org.opencds.cqf.cql.engine.fhir.data;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.SearchableLibraryIdentifier;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.assertEvaluationResult;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_ARRIVED;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_CANCELLED;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_FINISHED;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_PLANNED;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourcesMultiLibComplexDepsRetrieveProvider.ENCOUNTER_TRIAGED;

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

    private static final Set<String> ALL_EXPRESSIONS = Set.of("Encounters A", "Encounters B");

    private static Stream<Arguments> singleLibraryEvaluationParams() {
        return Stream.of(
                Arguments.of(
                        LIB_1A,
                        "Encounters A",
                        List.of(ENCOUNTER_ARRIVED, ENCOUNTER_CANCELLED, ENCOUNTER_FINISHED, ENCOUNTER_PLANNED, ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)
                ),
                Arguments.of(
                        LIB_1B,
                        "Encounters A",
                        List.of(ENCOUNTER_ARRIVED, ENCOUNTER_CANCELLED, ENCOUNTER_FINISHED, ENCOUNTER_PLANNED, ENCOUNTER_TRIAGED),
                        List.of(ENCOUNTER_ARRIVED)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("singleLibraryEvaluationParams")
    void singleLibraryEvaluation(SearchableLibraryIdentifier libraryIdentifier, String expressionName, List<IBaseResource> expectedEvaluatedResources, List<IBaseResource> expectedValues) {
        var engine = getCqlEngineForFhirNewLibMgr(false);

        var resultsSingleLib = engine.evaluate(libraryIdentifier.toIdentifier(), ALL_EXPRESSIONS);

        assertThat(resultsSingleLib, is(notNullValue()));

        log.info("resultsSingleLib: {}", printEvaluationResult(resultsSingleLib));

        assertEvaluationResult(resultsSingleLib, expressionName, expectedEvaluatedResources, expectedValues);
    }

    @Test
    void newWay() {
        var engine = getCqlEngineForFhirNewLibMgr(false);

        var resultsSingleLib = engine.evaluate(List.of(LIB_1A.toIdentifier()), ALL_EXPRESSIONS);

        assertThat(resultsSingleLib, is(notNullValue()));

        log.info("resultsSingleLib: {}", printEvaluationResult(resultsSingleLib));
    }

    @Test
    void newWayMulti() {
        var engine = getCqlEngineForFhirNewLibMgr(false);

        var resultsSingleLib = engine.evaluate(List.of(LIB_1A.toIdentifier(), LIB_1B.toIdentifier()), ALL_EXPRESSIONS);

        assertThat(resultsSingleLib, is(notNullValue()));

        log.info("resultsSingleLib: {}", printEvaluationResult(resultsSingleLib));
    }

    @Nonnull
    private CqlEngine getCqlEngineForFhirNewLibMgr(boolean expressionCaching) {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
                getEngineWithNewLibraryManager(), expressionCaching, r4ModelResolver, RETRIEVE_PROVIDER_COMPLEX);
    }
}
