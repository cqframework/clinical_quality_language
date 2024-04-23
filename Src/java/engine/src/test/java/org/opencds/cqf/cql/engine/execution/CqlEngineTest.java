package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.opencds.cqf.cql.engine.debug.DebugMap;
import org.testng.annotations.Test;

class CqlEngineTest extends CqlTestBase {

    @Test
    void testDebugMap() {

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
}
