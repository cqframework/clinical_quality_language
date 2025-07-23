package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.cqframework.cql.cql2elm.CqlIncludeException;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;

class CqlEngineMultipleLibrariesTest extends CqlTestBase {

    private static final DateTime _1900_01_01 = new DateTime("1900-01-01", ZoneOffset.UTC);
    private static final DateTime _1901_01_01 = new DateTime("1901-01-01", ZoneOffset.UTC);
    private static final DateTime _2021_01_01 = new DateTime("2021-01-01", ZoneOffset.UTC);
    private static final DateTime _2022_01_01 = new DateTime("2022-01-01", ZoneOffset.UTC);
    private static final DateTime _2023_01_01 = new DateTime("2023-01-01", ZoneOffset.UTC);
    private static final DateTime _2024_01_01 = new DateTime("2024-01-01", ZoneOffset.UTC);
    private static final DateTime _2031_01_01 = new DateTime("2031-01-01", ZoneOffset.UTC);
    private static final DateTime _2032_01_01 = new DateTime("2032-01-01", ZoneOffset.UTC);

    private static final Interval _1900_01_01_TO_1901_01_01 = new Interval(_1900_01_01, true, _1901_01_01, false);
    private static final Interval _2021_01_01_TO_2022_01_01 = new Interval(_2021_01_01, true, _2022_01_01, false);
    private static final Interval _2022_01_01_TO_2023_01_01 = new Interval(_2022_01_01, true, _2023_01_01, false);
    private static final Interval _2023_01_01_TO_2024_01_01 = new Interval(_2023_01_01, true, _2024_01_01, false);
    private static final Interval _2031_01_01_TO_2032_01_01 = new Interval(_2031_01_01, true, _2032_01_01, false);

    // LUKETODO:  errors for all libraries
    // LUKETODO:  errors for one of several libraries
    // LUKETODO:  assert evaluated resources

    @Override
    protected String getCqlSubdirectory() {
        return "multilib";
    }

    private DebugMap debugMap;
    private CqlEngine cqlEngine;

    @BeforeEach
    void setup() {
        debugMap = new DebugMap();
        cqlEngine = new CqlEngine(environment, Set.of(CqlEngine.Options.EnableExpressionCaching));
    }

    @Test
    void nonexistentLibrary() {
        var exceptionMessage = assertThrows(
                        CqlIncludeException.class,
                        () -> cqlEngine.evaluate(
                                List.of(toElmIdentifier("OtherName")), null, null, null, new DebugMap(), null))
                .getMessage();

        assertEquals(
                "Could not load source for library OtherName, version null, namespace uri null.", exceptionMessage);
    }

    @Test
    void libraryAndCqlMismatchedNames() {
        // The existing single library evaluation will tolerate name mismatches, however, this violates the CQL standard
        // Fixing that bug is out of scope for this work, but we definitely want to do the right thing going forward
        // with the new multiple library evaluation.
        var exceptionMessage = assertThrows(
                        CqlIncludeException.class,
                        () -> cqlEngine.evaluate(
                                List.of(toElmIdentifier("NameMismatch")), null, null, null, new DebugMap(), null))
                .getMessage();

        assertEquals(
                "Library NameMismatch was included with version null, but id: MismatchName and version null of the library was found.",
                exceptionMessage);
    }

    private static Stream<Arguments> libraryWithVersionQueriesParams() {
        return Stream.of(
                Arguments.of(toElmIdentifier("LibraryWithVersion")),
                Arguments.of(toElmIdentifier("LibraryWithVersion", "1.0.0")));
    }

    @ParameterizedTest
    @MethodSource("libraryWithVersionQueriesParams")
    void libraryWithVersionQueries(VersionedIdentifier libraryIdentifier) {
        var evalResultsForMultiLib = cqlEngine.evaluate(List.of(libraryIdentifier), null, null, null, debugMap, null);

        assertNotNull(evalResultsForMultiLib);
        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(1, libraryResults.size());
        assertTrue(evalResultsForMultiLib.getErrors().isEmpty());

        var evaluationResult = findResultsByLibId("LibraryWithVersion", libraryResults);
        assertEquals(5, evaluationResult.expressionResults.get("Number").value());
        assertEquals(
                _2031_01_01_TO_2032_01_01,
                evaluationResult.expressionResults.get("Period").value());
    }

    // LUKETODO: test with mismatched versions but not names
    // LUKETODO:

    // LUKETODO:  test expressions
    // LUKETODO:  set up more complex libraries
    // LUKETODO:  cache is by expression name, so 2 identical expresions will be essentially duplicated
    @Test
    void multipleLibrariesSimple() {
        var evalResultsForMultiLib = cqlEngine.evaluate(
                List.of(
                        toElmIdentifier("MultiLibrary1"),
                        toElmIdentifier("MultiLibrary2"),
                        toElmIdentifier("MultiLibrary3")),
                null,
                null,
                null,
                debugMap,
                null);

        assertNotNull(evalResultsForMultiLib);
        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(3, libraryResults.size());
        assertTrue(evalResultsForMultiLib.getErrors().isEmpty());

        var evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults);
        var evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults);
        var evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults);

        assertEquals(1, evaluationResult1.expressionResults.get("Number").value());
        assertEquals(2, evaluationResult2.expressionResults.get("Number").value());
        assertEquals(3, evaluationResult3.expressionResults.get("Number").value());

        assertEquals(
                _2021_01_01_TO_2022_01_01,
                evaluationResult1.expressionResults.get("Period").value());
        assertEquals(
                _2022_01_01_TO_2023_01_01,
                evaluationResult2.expressionResults.get("Period").value());
        assertEquals(
                _2023_01_01_TO_2024_01_01,
                evaluationResult3.expressionResults.get("Period").value());
    }

    // LUKETODO:  add another test with multiple expressions in the Set

    // LUKETODO:  add another test with different expressions in the Set,  unique to that library, such as "Number1",
    // "Number2", "Number3"

    @Test
    void multipleLibrariesWithParameters() {
        var evalResultsForMultiLib = cqlEngine.evaluate(
                List.of(
                        toElmIdentifier("MultiLibrary1"),
                        toElmIdentifier("MultiLibrary2"),
                        toElmIdentifier("MultiLibrary3")),
                null,
                null,
                Map.of("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null);

        assertNotNull(evalResultsForMultiLib);
        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(3, libraryResults.size());
        assertTrue(evalResultsForMultiLib.getErrors().isEmpty());

        var evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults);
        var evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults);
        var evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults);

        assertEquals(1, evaluationResult1.expressionResults.get("Number").value());
        assertEquals(2, evaluationResult2.expressionResults.get("Number").value());
        assertEquals(3, evaluationResult3.expressionResults.get("Number").value());

        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult1.expressionResults.get("Period").value());
        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult2.expressionResults.get("Period").value());
        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult3.expressionResults.get("Period").value());
    }

    @Test
    void multipleLibrariesOneInvalid() {
        var evalResultsForMultiLib = cqlEngine.evaluate(
                List.of(
                        toElmIdentifier("MultiLibrary1"),
                        toElmIdentifier("MultiLibrary2"),
                        toElmIdentifier("MultiLibrary3"),
                        toElmIdentifier("MultiLibraryBad")),
                null,
                null,
                Map.of("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null);

        assertNotNull(evalResultsForMultiLib);

        assertFalse(evalResultsForMultiLib.getErrors().isEmpty());
        var errors = evalResultsForMultiLib.getErrors();
        assertEquals(1, errors.size());
        var errorEntry = errors.entrySet().iterator().next();
        assertEquals(SearchableLibraryIdentifier.fromId("MultiLibraryBad"), errorEntry.getKey());
        assertEquals("library MultiLibraryBad loaded, but had errors: Syntax error at define", errorEntry.getValue());

        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(3, libraryResults.size()); // there is no eval result for MultiLibraryBad

        var evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults);
        var evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults);
        var evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults);

        assertEquals(1, evaluationResult1.expressionResults.get("Number").value());
        assertEquals(2, evaluationResult2.expressionResults.get("Number").value());
        assertEquals(3, evaluationResult3.expressionResults.get("Number").value());

        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult1.expressionResults.get("Period").value());
        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult2.expressionResults.get("Period").value());
        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult3.expressionResults.get("Period").value());
    }

    private EvaluationResult findResultsByLibId(
            String libId, Map<SearchableLibraryIdentifier, EvaluationResult> results) {
        return results.entrySet().stream()
                .filter(x -> x.getKey().matches(libId))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }
}
