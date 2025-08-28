package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.opencds.cqf.cql.engine.exception.CqlException;
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
    private static final VersionedIdentifier MULTI_LIBRARY_1 = toElmIdentifier("MultiLibrary1");
    private static final VersionedIdentifier MULTI_LIBRARY_2 = toElmIdentifier("MultiLibrary2");
    private static final VersionedIdentifier MULTI_LIBRARY_3 = toElmIdentifier("MultiLibrary3");
    private static final String LIBRARY_WITH_VERSION = "LibraryWithVersion";
    private static final String VERSION_1_0_0 = "1.0.0";

    @Override
    protected String getCqlSubdirectory() {
        return "multilib";
    }

    private DebugMap debugMap;
    private CqlEngine cqlEngineWithNoOptions;
    private CqlEngine cqlEngineWithOptions;

    @BeforeEach
    void setup() {
        debugMap = new DebugMap();
        cqlEngineWithNoOptions = new CqlEngine(environment, null);
        cqlEngineWithOptions = new CqlEngine(environment, Set.of(CqlEngine.Options.EnableExpressionCaching));
    }

    @Test
    void nonexistentLibrary() {
        var versionedIdentifier = toElmIdentifier("OtherName");
        var versionedIdentifiers = List.of(versionedIdentifier);
        var newDebugMap = new DebugMap();

        var exceptionMessage = assertThrows(CqlIncludeException.class, () -> {
                    cqlEngineWithNoOptions.evaluate(versionedIdentifiers, null, null, null, newDebugMap, null);
                })
                .getMessage();

        assertEquals(
                "Could not load source for library OtherName, version null, namespace uri null.", exceptionMessage);
    }

    @Test
    void libraryAndCqlMismatchedNames() {
        var versionedIdentifier = toElmIdentifier("NameMismatch");
        var versionedIdentifiers = List.of(versionedIdentifier);
        var newDebugMap = new DebugMap();

        // The existing single library evaluation will tolerate name mismatches, however, this violates the CQL standard
        // Fixing that bug is out of scope for this work, but we definitely want to do the right thing going forward
        // with the new multiple library evaluation.
        var exceptionMessage = assertThrows(CqlIncludeException.class, () -> {
                    cqlEngineWithOptions.evaluate(versionedIdentifiers, null, null, null, newDebugMap, null);
                })
                .getMessage();

        assertEquals(
                "Library NameMismatch was included with version null, but id: MismatchName and version null of the library was found.",
                exceptionMessage);
    }

    private static Stream<Arguments> libraryWithVersionQueriesParams() {
        return Stream.of(
                Arguments.of(toElmIdentifier(LIBRARY_WITH_VERSION)),
                Arguments.of(toElmIdentifier(LIBRARY_WITH_VERSION, VERSION_1_0_0)));
    }

    @ParameterizedTest
    @MethodSource("libraryWithVersionQueriesParams")
    void libraryWithVersionQueries(VersionedIdentifier libraryIdentifier) {
        var evalResultsForMultiLib =
                cqlEngineWithOptions.evaluate(List.of(libraryIdentifier), null, null, null, debugMap, null);

        assertNotNull(evalResultsForMultiLib);
        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(1, libraryResults.size());
        assertFalse(evalResultsForMultiLib.hasExceptions());

        var evaluationResult = findResultsByLibId(LIBRARY_WITH_VERSION, libraryResults);
        assertEquals(5, evaluationResult.expressionResults.get("Number").value());
        assertEquals(
                _2031_01_01_TO_2032_01_01,
                evaluationResult.expressionResults.get("Period").value());
    }

    // Various bespoke assertions to increase test coverage
    @Test
    void extraTestCoverage() {
        var versionedIdent =
                new VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion(VERSION_1_0_0);
        var versionedIdents = List.of(versionedIdent);

        var evalResultsForMultiLib = cqlEngineWithOptions.evaluate(versionedIdents, null, null, null, debugMap, null);

        assertNotNull(evalResultsForMultiLib);
        assertNull(evalResultsForMultiLib.getExceptionFor(versionedIdent));
        assertNull(evalResultsForMultiLib.getExceptionFor(versionedIdent));
        assertNull(evalResultsForMultiLib.getExceptionFor(new VersionedIdentifier().withId("fake")));
        assertNull(evalResultsForMultiLib.getExceptionFor(
                new VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion(null)));
        assertNull(evalResultsForMultiLib.getExceptionFor(
                new VersionedIdentifier().withId(LIBRARY_WITH_VERSION).withVersion("fake")));
    }

    @Test
    void multipleLibrariesSimple() {
        var evalResultsForMultiLib = cqlEngineWithOptions.evaluate(
                List.of(MULTI_LIBRARY_1, MULTI_LIBRARY_2, MULTI_LIBRARY_3), null, null, null, debugMap, null);

        assertNotNull(evalResultsForMultiLib);
        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(3, libraryResults.size());
        assertFalse(evalResultsForMultiLib.hasExceptions());

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

    @Test
    void multipleLibrariesMismatchedVersions() {
        var versionedIdentifierBad1 = toElmIdentifier("MultiLibrary1", "bad");
        var versionedIdentifierBad2 = toElmIdentifier("MultiLibrary2", "bad");
        var versionedIdentifierBad3 = toElmIdentifier("MultiLibrary3", "bad");
        var versionedIdentifiers = List.of(versionedIdentifierBad1, versionedIdentifierBad2, versionedIdentifierBad3);

        var exception = assertThrows(CqlIncludeException.class, () -> {
            cqlEngineWithOptions.evaluate(versionedIdentifiers, null, null, null, debugMap, null);
        });

        assertThat(
                exception.getMessage(),
                containsString(
                        "Library MultiLibrary1 was included with version bad, but id: MultiLibrary1 and version 1.0.0 of the library was found."));
    }

    @Test
    void multipleLibrariesWithParameters() {
        var evalResultsForMultiLib = cqlEngineWithOptions.evaluate(
                List.of(MULTI_LIBRARY_1, MULTI_LIBRARY_2, MULTI_LIBRARY_3),
                null,
                null,
                Map.of("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null);

        sanityCheckForMultiLib(evalResultsForMultiLib, MULTI_LIBRARY_1);
        sanityCheckForMultiLib(evalResultsForMultiLib, MULTI_LIBRARY_2);
        sanityCheckForMultiLib(evalResultsForMultiLib, MULTI_LIBRARY_3);

        assertThrows(IllegalStateException.class, evalResultsForMultiLib::getOnlyResultOrThrow);
        assertNotNull(evalResultsForMultiLib);
        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(3, libraryResults.size());
        assertFalse(evalResultsForMultiLib.hasExceptions());

        var evaluationResult1 = findResultsByLibId("MultiLibrary1", libraryResults);
        var evaluationResult2 = findResultsByLibId("MultiLibrary2", libraryResults);
        var evaluationResult3 = findResultsByLibId("MultiLibrary3", libraryResults);

        assertEquals(1, evaluationResult1.expressionResults.get("Number").value());
        assertEquals(2, evaluationResult2.expressionResults.get("Number").value());
        assertEquals(3, evaluationResult3.expressionResults.get("Number").value());

        assertEquals("Uno", evaluationResult1.expressionResults.get("Name").value());
        assertEquals("Dos", evaluationResult2.expressionResults.get("Name").value());
        assertEquals("Tres", evaluationResult3.expressionResults.get("Name").value());

        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult1.expressionResults.get("Period").value());
        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult2.expressionResults.get("Period").value());
        assertEquals(
                _1900_01_01_TO_1901_01_01,
                evaluationResult3.expressionResults.get("Period").value());

        // Expressions unique to the libraries in question
        assertEquals(
                "MultiLibrary1",
                evaluationResult1.expressionResults.get("MultiLibraryIdent1").value());
        assertEquals(
                "One",
                evaluationResult1.expressionResults.get("MultiLibraryValue1").value());

        assertEquals(
                "MultiLibrary2",
                evaluationResult2.expressionResults.get("MultiLibraryIdent2").value());
        assertEquals(
                "Two",
                evaluationResult2.expressionResults.get("MultiLibraryValue2").value());

        assertEquals(
                "MultiLibrary3",
                evaluationResult3.expressionResults.get("MultiLibraryIdent3").value());
        assertEquals(
                "Three",
                evaluationResult3.expressionResults.get("MultiLibraryValue3").value());
    }

    @Test
    void multipleLibrariesWithExpressionUniqueToASingleLib() {
        var evalResultsForMultiLib = cqlEngineWithOptions.evaluate(
                List.of(MULTI_LIBRARY_1, MULTI_LIBRARY_2, MULTI_LIBRARY_3),
                // One expression common to all libraries, one each unique to a different single library
                Set.of("Number", "MultiLibraryIdent1", "MultiLibraryValue2"),
                null,
                null,
                debugMap,
                null);

        assertNotNull(evalResultsForMultiLib);
        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(0, libraryResults.size());
        assertTrue(evalResultsForMultiLib.hasExceptions());
        var exceptions = evalResultsForMultiLib.getExceptions();
        assertEquals(3, exceptions.size());

        assertThat(
                exceptions.get(MULTI_LIBRARY_1).getMessage(),
                containsString(
                        "Could not resolve expression reference 'MultiLibraryValue2' in library 'MultiLibrary1'."));
        assertThat(
                exceptions.get(MULTI_LIBRARY_2).getMessage(),
                containsString(
                        "Could not resolve expression reference 'MultiLibraryIdent1' in library 'MultiLibrary2'."));
        // We may get either one of two exceptions here, depending on the order of evaluation
        assertThat(
                exceptions.get(MULTI_LIBRARY_3).getMessage(), containsString("Could not resolve expression reference"));
    }

    @Test
    void multipleLibrariesWithSubsetOfAllCommonExpressions() {
        var evalResultsForMultiLib = cqlEngineWithOptions.evaluate(
                List.of(MULTI_LIBRARY_1, MULTI_LIBRARY_2, MULTI_LIBRARY_3),
                // We're leaving out "Name" here
                Set.of("Number", "Period"),
                null,
                Map.of("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null);

        assertNotNull(evalResultsForMultiLib);
        var libraryResults = evalResultsForMultiLib.getResults();
        assertEquals(3, libraryResults.size());
        assertFalse(evalResultsForMultiLib.hasExceptions());
        assertTrue(evalResultsForMultiLib.getExceptions().isEmpty());
        assertFalse(evalResultsForMultiLib.hasWarnings());

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

        assertNull(evaluationResult1.expressionResults.get("Name"));
        assertNull(evaluationResult2.expressionResults.get("Name"));
        assertNull(evaluationResult3.expressionResults.get("Name"));
    }

    @Test
    void singleLibraryInvalid() {
        var evalResultsForMultiLib = cqlEngineWithOptions.evaluate(
                List.of(toElmIdentifier("MultiLibraryBad", "0.1")),
                null,
                null,
                Map.of("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null);

        var exception = assertThrows(CqlException.class, evalResultsForMultiLib::getOnlyResultOrThrow);
        assertThat(
                exception.getMessage(),
                containsString("Library MultiLibraryBad-0.1 loaded, but had errors: Syntax error at define"));
    }

    @Test
    void multipleLibrariesOneInvalid() {
        var versionedIdentifierBad = toElmIdentifier("MultiLibraryBad", "0.1");
        var evalResultsForMultiLib = cqlEngineWithOptions.evaluate(
                List.of(MULTI_LIBRARY_1, MULTI_LIBRARY_2, MULTI_LIBRARY_3, versionedIdentifierBad),
                null,
                null,
                Map.of("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null);

        assertNotNull(evalResultsForMultiLib);
        assertFalse(evalResultsForMultiLib.getExceptions().isEmpty());
        var exceptions = evalResultsForMultiLib.getExceptions();
        assertEquals(1, exceptions.size());
        assertTrue(evalResultsForMultiLib.containsExceptionsFor(versionedIdentifierBad));
        // Search for the exception with a versioned identifier:  This should also work
        assertTrue(evalResultsForMultiLib.containsExceptionsFor(new VersionedIdentifier().withId("MultiLibraryBad")));
        var exceptionEntry = exceptions.entrySet().iterator().next();
        assertEquals(new VersionedIdentifier().withId("MultiLibraryBad").withVersion("0.1"), exceptionEntry.getKey());
        var exception = exceptionEntry.getValue();
        assertNotNull(exception);
        assertEquals(
                "Library MultiLibraryBad-0.1 loaded, but had errors: Syntax error at define", exception.getMessage());
        assertNull(evalResultsForMultiLib.getResultFor(versionedIdentifierBad));

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

    private EvaluationResult findResultsByLibId(String libId, Map<VersionedIdentifier, EvaluationResult> results) {
        return results.entrySet().stream()
                .filter(x -> x.getKey().getId().equals(libId))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }

    private static void sanityCheckForMultiLib(
            EvaluationResultsForMultiLib evalResultsForMultiLib, VersionedIdentifier libraryIdentifier) {
        assertTrue(evalResultsForMultiLib.containsResultsFor(libraryIdentifier));

        var bogusId = new VersionedIdentifier().withId("bogus");
        assertFalse(evalResultsForMultiLib.containsResultsFor(bogusId));
        assertFalse(evalResultsForMultiLib.containsWarningsFor(new VersionedIdentifier().withId("bogus")));
        assertFalse(evalResultsForMultiLib.containsExceptionsFor(new VersionedIdentifier().withId("bogus")));

        var bogusVersion = libraryIdentifier.withVersion("bogus");
        assertFalse(evalResultsForMultiLib.containsResultsFor(bogusVersion));
        assertFalse(evalResultsForMultiLib.containsWarningsFor(bogusVersion));
        assertFalse(evalResultsForMultiLib.containsExceptionsFor(bogusVersion));

        // versionless identifier searches should work as well
        assertTrue(evalResultsForMultiLib.containsResultsFor(libraryIdentifier.withVersion(null)));
        assertThrows(IllegalStateException.class, evalResultsForMultiLib::getOnlyResultOrThrow);
        assertFalse(evalResultsForMultiLib.containsExceptionsFor(libraryIdentifier));
        assertFalse(evalResultsForMultiLib.containsWarningsFor(libraryIdentifier));
        assertTrue(evalResultsForMultiLib.getExceptions().isEmpty());
        assertTrue(evalResultsForMultiLib.getWarnings().isEmpty());
        assertNotNull(evalResultsForMultiLib.getResultFor(libraryIdentifier));
        assertNull(evalResultsForMultiLib.getExceptionFor(libraryIdentifier));
        assertNull(evalResultsForMultiLib.getWarningFor(libraryIdentifier));
    }
}
