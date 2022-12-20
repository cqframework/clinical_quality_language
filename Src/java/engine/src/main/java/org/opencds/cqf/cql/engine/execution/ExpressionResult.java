package org.opencds.cqf.cql.engine.execution;

import java.util.List;

public class ExpressionResult {
    protected Object value;
    protected List<Object> evaluatedResources;

    public ExpressionResult(Object value, List<Object> evaluatedResources) {
        this.value = value;
        this.evaluatedResources = evaluatedResources;
    }

    public Object value() {
        return value;
    }

    public List<Object> evaluatedResources() {
        return  this.evaluatedResources;
    }
}
