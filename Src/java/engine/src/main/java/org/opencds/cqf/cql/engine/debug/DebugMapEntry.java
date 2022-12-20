package org.opencds.cqf.cql.engine.debug;

public class DebugMapEntry {
    private DebugLocator locator;
    public DebugLocator getLocator() {
        return locator;
    }

    private DebugAction action;
    public DebugAction getAction() {
        return this.action;
    }

    public DebugMapEntry(DebugLocator locator, DebugAction action) {
        if (locator == null) {
            throw new IllegalArgumentException("locator required");
        }
        this.locator = locator;
        this.action = action;
    }
}
