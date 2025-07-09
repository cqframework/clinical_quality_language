package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;

class CqlEngineTest extends CqlTestBase {

    private static final DateTime _1900_01_01 = new DateTime("1900-01-01", ZoneOffset.UTC);
    private static final DateTime _1901_01_01 = new DateTime("1901-01-01", ZoneOffset.UTC);
    private static final DateTime _2021_01_01 = new DateTime("2021-01-01", ZoneOffset.UTC);
    private static final DateTime _2022_01_01 = new DateTime("2022-01-01", ZoneOffset.UTC);
    private static final DateTime _2023_01_01 = new DateTime("2023-01-01", ZoneOffset.UTC);
    private static final DateTime _2024_01_01 = new DateTime("2024-01-01", ZoneOffset.UTC);

    private static final Interval _1900_01_01_TO_1901_01_01 = new Interval(_1900_01_01, true, _1901_01_01, false);
    private static final Interval _2021_01_01_TO_2022_01_01 = new Interval(_2021_01_01, true, _2022_01_01, false);
    private static final Interval _2022_01_01_TO_2023_01_01 = new Interval(_2022_01_01, true, _2023_01_01, false);
    private static final Interval _2023_01_01_TO_2024_01_01 = new Interval(_2023_01_01, true, _2024_01_01, false);

    @Test
    void debugMap() {

        // The specific library isn't important, just that it has a debug map
        var debugMap = new DebugMap();
        debugMap.setIsLoggingEnabled(true);
        var results = engine.evaluate(toElmIdentifier("CqlEngineTest"), null, null, null, debugMap);

        var libraryDebug = results.getDebugResult()
                .getLibraryResults()
                .get("CqlEngineTest")
                .getResults();

        assertNotNull(libraryDebug);

        // Find the debug result for the AnInteger expression
        // It's indexed by location
        var result = libraryDebug.keySet().stream()
                .filter(e -> e.getLocator().equals("6:1-6:21"))
                .findFirst();

        assertTrue(result.isPresent());

        var debugResult = libraryDebug.get(result.get());
        assertEquals(1, debugResult.size());
    }

    @Test
    public void hedisCompatibility() {
        var libraryResult = engine.evaluate(toElmIdentifier("HedisCompatibilityTest"));
        var result = libraryResult.expressionResults.get("QuantityListIncludes").value();
        assertFalse((Boolean) result);

        result = libraryResult.expressionResults.get("ReturnUnspecified").value();
        assertInstanceOf(List.class, result);
        assertEquals(2, ((List<?>) result).size());

        result = libraryResult.expressionResults.get("ReturnAll").value();
        assertInstanceOf(List.class, result);
        assertEquals(5, ((List<?>) result).size());

        result = libraryResult.expressionResults.get("ReturnDistinct").value();
        assertInstanceOf(List.class, result);
        assertEquals(2, ((List<?>) result).size());

        result = libraryResult.expressionResults.get("Test Null Tuple").value();
        assertNull(result);

        engine.getState().getEngineOptions().add(CqlEngine.Options.EnableHedisCompatibilityMode);
        libraryResult = engine.evaluate(toElmIdentifier("HedisCompatibilityTest"));
        // equivalent semantics for lists
        result = libraryResult.expressionResults.get("QuantityListIncludes").value();
        assertTrue((Boolean) result);

        // no "distinct" behavior for lists
        result = libraryResult.expressionResults.get("ReturnUnspecified").value();
        assertInstanceOf(List.class, result);
        assertEquals(5, ((List<?>) result).size());

        result = libraryResult.expressionResults.get("ReturnAll").value();
        assertInstanceOf(List.class, result);
        assertEquals(5, ((List<?>) result).size());

        result = libraryResult.expressionResults.get("ReturnDistinct").value();
        assertInstanceOf(List.class, result);
        assertEquals(5, ((List<?>) result).size());

        result = libraryResult.expressionResults.get("Test Null Tuple").value();
        assertNull(result);
    }

    // LUKETODO:  set up more complex libraries
    // LUKETODO:  cache is by expression name, so 2 identical expresions will be essentially duplicated
    @Test
    void multipleLibrariesSimple() {
        var debugMap = new DebugMap();
        var myEngine = new CqlEngine(environment, Set.of(CqlEngine.Options.EnableExpressionCaching));
        var libraryResults = myEngine.evaluate(
                List.of(
                        toElmIdentifier("MultiLibrary1"),
                        toElmIdentifier("MultiLibrary2"),
                        toElmIdentifier("MultiLibrary3")),
                null,
                null,
                debugMap,
                null);

        assertNotNull(libraryResults);
        assertEquals(3, libraryResults.size());

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

    // LUKETODO:  add another test with different expressions in the Set,  unique to that library, such as "Number1", "Number2", "Number3"

    @Test
    void multipleLibrariesWithParameters() {
        var debugMap = new DebugMap();
        var myEngine = new CqlEngine(environment, Set.of(CqlEngine.Options.EnableExpressionCaching));
        var libraryResults = myEngine.evaluate(
                List.of(
                        toElmIdentifier("MultiLibrary1"),
                        toElmIdentifier("MultiLibrary2"),
                        toElmIdentifier("MultiLibrary3")),
                null,
                Map.of("Measurement Period", _1900_01_01_TO_1901_01_01),
                debugMap,
                null);

        assertNotNull(libraryResults);
        assertEquals(3, libraryResults.size());

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
}
