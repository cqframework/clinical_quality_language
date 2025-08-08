package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.opencds.cqf.cql.engine.exception.CqlException;

class CqlEngineTest extends CqlTestBase {

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
    public void invalidCql() {
        var exception = assertThrows(CqlException.class, () -> engine.evaluate(toElmIdentifier("Invalid")));
        assertThat(exception.getMessage(), containsString("Library Invalid loaded, but had errors:"));
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
}
