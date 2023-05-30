package org.opencds.cqf.cql.engine.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.elm.execution.Executable;
import org.opencds.cqf.cql.engine.exception.CqlException;

public class DebugResult {
    private Map<String, DebugLibraryResultEntry> libraryResults;
    private ArrayList<CqlException> messages;

    public DebugResult() {
        libraryResults = new HashMap<String, DebugLibraryResultEntry>();
        messages = new ArrayList<CqlException>();
    }

    public void logDebugResult(Executable node, Library currentLibrary, Object result, DebugAction action) {
        try {
            DebugLibraryResultEntry libraryResultEntry = libraryResults.get(currentLibrary.getIdentifier().getId());
            if (libraryResultEntry == null) {
                libraryResultEntry = new DebugLibraryResultEntry(currentLibrary.getIdentifier().getId());
                libraryResults.put(libraryResultEntry.getLibraryName(), libraryResultEntry);
            }
            if (libraryResultEntry != null) {
                libraryResultEntry.logDebugResultEntry(node, result);
            }

            if (action == DebugAction.LOG) {
                DebugUtilities.logDebugResult(node, currentLibrary, result);
            }
        }
        catch (Exception e) {
            // do nothing, an exception logging debug helps no one
        }
    }

    public void logDebugError(CqlException exception) {
        messages.add(exception);
    }

    public List<CqlException> getMessages() {
        return messages;
    }

    public Map<String, DebugLibraryResultEntry> getLibraryResults() {
        return libraryResults;
    }
}
