package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.debug.DebugMap;

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
    public void equivalentInOption() {
        var libraryResult = engine.evaluate(toElmIdentifier("CqlEquivalentInOptionTest"));
        var expressionResult =
                libraryResult.expressionResults.get("QuantityListIncludes").value();
        assertTrue((Boolean) expressionResult);

        engine.getState().getEngineOptions().add(CqlEngine.Options.DisableEquivalentIn);
        libraryResult = engine.evaluate(toElmIdentifier("CqlEquivalentInOptionTest"));
        expressionResult =
                libraryResult.expressionResults.get("QuantityListIncludes").value();
        assertFalse((Boolean) expressionResult);
    }
}
