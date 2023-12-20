package org.opencds.cqf.cql.engine.debug;

import java.util.HashMap;
import java.util.Map;
import org.hl7.elm.r1.Element;

public class DebugLibraryMapEntry {
    private String libraryName;

    public String getLibraryName() {
        return this.libraryName;
    }

    private Map<String, DebugMapEntry> nodeEntries;
    private Map<String, DebugMapEntry> locationEntries;

    public DebugLibraryMapEntry(String libraryName) {
        this.libraryName = libraryName;
        nodeEntries = new HashMap<String, DebugMapEntry>();
        locationEntries = new HashMap<String, DebugMapEntry>();
    }

    public DebugAction shouldDebug(Element node) {
        if (node != null) {
            DebugMapEntry nodeEntry = nodeEntries.get(node.getLocalId());
            if (nodeEntry != null && nodeEntry.getAction() != DebugAction.NONE) {
                return nodeEntry.getAction();
            }

            for (DebugMapEntry entry : locationEntries.values()) {
                if (node.getLocator() != null) {
                    Location nodeLocation = Location.fromLocator(node.getLocator());
                    if (entry.getLocator().getLocation().includes(nodeLocation)
                            && entry.getAction() != DebugAction.NONE) {
                        return entry.getAction();
                    }
                }
            }
        }

        return DebugAction.NONE;
    }

    public void addEntry(DebugLocator debugLocator, DebugAction action) {
        addEntry(new DebugMapEntry(debugLocator, action));
    }

    public void addEntry(DebugMapEntry entry) {
        switch (entry.getLocator().getLocatorType()) {
            case NODE_ID:
                nodeEntries.put(entry.getLocator().getLocator(), entry);
                break;
            case LOCATION:
                locationEntries.put(entry.getLocator().getLocator(), entry);
                break;
            default:
                throw new IllegalArgumentException(
                        "Library debug map entry can only contain node id or location debug entries");
        }
    }

    public void removeEntry(DebugLocator debugLocator) {
        switch (debugLocator.getLocatorType()) {
            case NODE_ID:
                nodeEntries.remove(debugLocator.getLocator());
                break;
            case LOCATION:
                locationEntries.remove(debugLocator.getLocator());
                break;
            default:
                throw new IllegalArgumentException(
                        "Library debug map entry only contains node id or location debug entries");
        }
    }
}
