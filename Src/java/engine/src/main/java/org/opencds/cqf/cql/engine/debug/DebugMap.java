package org.opencds.cqf.cql.engine.debug;

import java.util.HashMap;
import java.util.Map;

import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.elm.execution.Executable;

public class DebugMap {

    private Map<String, DebugLibraryMapEntry> libraryMaps;
    private Map<String, DebugMapEntry> nodeTypeEntries;
    private Map<String, DebugMapEntry> exceptionTypeEntries;

    public DebugMap() {
        libraryMaps = new HashMap<String, DebugLibraryMapEntry>();
        nodeTypeEntries = new HashMap<String, DebugMapEntry>();
        exceptionTypeEntries = new HashMap<String, DebugMapEntry>();
    }

    public DebugAction shouldDebug(Exception e) {
        if (exceptionTypeEntries.size() == 0) {
            return DebugAction.LOG;
        }
        else {
            DebugMapEntry exceptionTypeEntry = exceptionTypeEntries.get(e.getClass().getSimpleName());
            if (exceptionTypeEntry != null)
                return exceptionTypeEntry.getAction();
        }

        // Exceptions are always logged (unless explicitly disabled by a DebugAction.NONE for the specific type)
        return DebugAction.LOG;
    }

    public DebugAction shouldDebug(Executable node, Library currentLibrary) {
        DebugLibraryMapEntry libraryMap = libraryMaps.get(currentLibrary.getIdentifier().getId());
        if (libraryMap != null) {
            DebugAction action = libraryMap.shouldDebug(node);
            if (action != DebugAction.NONE) {
                return action;
            }
        }

        DebugMapEntry nodeEntry = nodeTypeEntries.get(node.getClass().getSimpleName());
        if (nodeEntry != null && nodeEntry.getAction() != DebugAction.NONE) {
            return nodeEntry.getAction();
        }

        if (isLoggingEnabled) {
            return DebugAction.LOG;
        }

        if (isCoverageEnabled) {
            return DebugAction.TRACE;
        }

        return DebugAction.NONE;
    }

    private DebugLibraryMapEntry getLibraryMap(String libraryName) {
        return libraryMaps.get(libraryName);
    }

    private DebugLibraryMapEntry ensureLibraryMap(String libraryName) {
        DebugLibraryMapEntry libraryMap = libraryMaps.get(libraryName);
        if (libraryMap == null) {
            libraryMap = new DebugLibraryMapEntry(libraryName);
            libraryMaps.put(libraryName, libraryMap);
        }

        return libraryMap;
    }

    // private void addLibraryMapEntry(String libraryName, DebugLibraryMapEntry libraryMapEntry) {
    //     libraryMaps.put(libraryName, libraryMapEntry);
    // }

    public void addDebugEntry(DebugLocator debugLocator, DebugAction action) {
        addDebugEntry(null, debugLocator, action);
    }

    public void addDebugEntry(String libraryName, DebugLocator debugLocator, DebugAction action) {
        switch (debugLocator.getLocatorType()) {
            case NODE_TYPE: nodeTypeEntries.put(debugLocator.getLocator(), new DebugMapEntry(debugLocator, action)); break;
            case EXCEPTION_TYPE: exceptionTypeEntries.put(debugLocator.getLocator(), new DebugMapEntry(debugLocator, action)); break;
            default: {
                if (libraryName != null) {
                    DebugLibraryMapEntry libraryMap = ensureLibraryMap(libraryName);
                    libraryMap.addEntry(debugLocator, action);
                }
                else {
                    throw new IllegalArgumentException("Library entries must have a library name specified");
                }
            }
        }
    }

    public void removeDebugEntry(String libraryName, DebugLocator debugLocator) {
        switch (debugLocator.getLocatorType()) {
            case NODE_TYPE: nodeTypeEntries.remove(debugLocator.getLocator()); break;
            case EXCEPTION_TYPE: exceptionTypeEntries.remove(debugLocator.getLocator()); break;
            default: {
                if (libraryName != null) {
                    DebugLibraryMapEntry libraryMap = getLibraryMap(libraryName);
                    if (libraryMap != null) {
                        libraryMap.removeEntry(debugLocator);
                    }
                }
                else {
                    throw new IllegalArgumentException("Library entries must have a library name specified");
                }
            }
        }
    }

    public void removeDebugEntry(DebugLocator debugLocator) {
        removeDebugEntry(null, debugLocator);
    }

    private boolean isLoggingEnabled;
    public boolean getIsLoggingEnabled() {
        return isLoggingEnabled;
    }
    public void setIsLoggingEnabled(boolean isLoggingEnabled) {
        this.isLoggingEnabled = isLoggingEnabled;
    }

    private boolean isCoverageEnabled;
    public boolean getIsCoverageEnabled() {
        return isCoverageEnabled;
    }
    public void setIsCoverageEnabled(boolean isCoverageEnabled) {
        this.isCoverageEnabled = isCoverageEnabled;
    }
}
