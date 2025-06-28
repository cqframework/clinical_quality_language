package org.opencds.cqf.cql.engine.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Library;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.Profile;

public class DebugResult {
    private final Map<String, DebugLibraryResultEntry> libraryResults;
    private final ArrayList<CqlException> messages;
    private Profile profile = null;

    public DebugResult() {
        libraryResults = new HashMap<>();
        messages = new ArrayList<>();
    }

    public void logDebugResult(Element node, Library currentLibrary, Object result, DebugAction action) {
        if (action == DebugAction.NONE) {
            return;
        }

        try {
            var libraryResultEntry =
                    libraryResults.get(currentLibrary.getIdentifier().getId());
            if (libraryResultEntry == null) {
                libraryResultEntry = new DebugLibraryResultEntry(
                        currentLibrary.getIdentifier().getId());
                libraryResults.put(libraryResultEntry.getLibraryName(), libraryResultEntry);
            }
            libraryResultEntry.logDebugResultEntry(node, result);

            if (action == DebugAction.LOG) {
                DebugUtilities.logDebugResult(node, currentLibrary, result);
            }
        } catch (Exception e) {
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

    public Profile getProfile() {
        return this.profile;
    }

    public Profile ensureProfile() {
        if (this.profile == null) {
            this.profile = new Profile();
        }
        return this.profile;
    }
}
