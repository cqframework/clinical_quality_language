package org.opencds.cqf.cql.engine.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.elm.r1.Element;

public class DebugLibraryResultEntry {
    private String libraryName;
    public String getLibraryName() {
        return this.libraryName;
    }

    public DebugLibraryResultEntry(String libraryName) {
        this.libraryName = libraryName;
        this.results = new HashMap<DebugLocator, List<DebugResultEntry>>();
    }

    private Map<DebugLocator, List<DebugResultEntry>> results;
    public Map<DebugLocator, List<DebugResultEntry>> getResults() {
        return results;
    }

    private void logDebugResult(DebugLocator locator, Object result) {
        if (!results.containsKey(locator)) {
            results.put(locator, new ArrayList<DebugResultEntry>());
        }
        List<DebugResultEntry> debugResults = results.get(locator);
        debugResults.add(new DebugResultEntry(result));
    }

    public void logDebugResultEntry(Element node, Object result) {
        if (node instanceof Element) {
            Element element = (Element)node;
            if (element.getLocalId() != null) {
                DebugLocator locator = new DebugLocator(DebugLocator.DebugLocatorType.NODE_ID, element.getLocalId());
                logDebugResult(locator, result);
            }

            if (element.getLocator() != null) {
                DebugLocator locator = new DebugLocator(Location.fromLocator(element.getLocator()));
                logDebugResult(locator, result);
            }
        }
        else {
            DebugLocator locator = new DebugLocator(DebugLocator.DebugLocatorType.NODE_TYPE, node.getClass().getSimpleName());
            logDebugResult(locator, result);
        }
    }
}
