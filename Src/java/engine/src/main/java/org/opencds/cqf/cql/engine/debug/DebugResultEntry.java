package org.opencds.cqf.cql.engine.debug;

public class DebugResultEntry {
    private Object value;

    public DebugResultEntry(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }
}
