package org.opencds.cqf.cql.engine.fhir.data;

import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.SearchableLibraryIdentifier;

import javax.annotation.Nonnull;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.opencds.cqf.cql.engine.fhir.data.EvaluatedResourceTestUtils.RETRIEVE_PROVIDER;

/**
 * See EvaluatedResourcesMultiLibComplexDepsTest.md for a mermaid diagram of the library dependencies
 */
class EvaluatedResourcesMultiLibComplexDepsTest extends FhirExecutionMultiLibTestBase {
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

    @Test
    void oldWay() {

    }

    @Test
    void newWay() {
        var engine = getCqlEngineForFhirNewLibMgr(false);

        var resultsSingleLib = engine.evaluate(getFirstLibraryIdentifierAsList(), ALL_EXPRESSIONS);

        assertThat(resultsSingleLib, is(notNullValue()));
    }

    @Nonnull
    private CqlEngine getCqlEngineForFhirNewLibMgr(boolean expressionCaching) {
        return EvaluatedResourceTestUtils.getCqlEngineForFhir(
                getEngineWithNewLibraryManager(), expressionCaching, r4ModelResolver, RETRIEVE_PROVIDER);
    }
}
