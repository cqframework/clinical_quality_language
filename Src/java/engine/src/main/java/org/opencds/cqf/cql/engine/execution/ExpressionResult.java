package org.opencds.cqf.cql.engine.execution;

import java.util.Set;

public class ExpressionResult {
    protected Object value;
    protected Set<Object> evaluatedResources;

    public ExpressionResult(Object value, Set<Object> evaluatedResources) {
        this.value = value;
        this.evaluatedResources = evaluatedResources;
    }

    public Object value() {
        return value;
    }

    public Set<Object> evaluatedResources() {
        return  this.evaluatedResources;
    }
}
