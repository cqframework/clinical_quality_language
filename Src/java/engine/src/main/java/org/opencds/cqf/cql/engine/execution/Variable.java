package org.opencds.cqf.cql.engine.execution;

import java.util.Objects;

public class Variable {
    private String name;
    private Object value;
    // for AliasEvaluator
    private boolean isList;

    public Variable(String name) {
        this.name = Objects.requireNonNull(name, "name can not be null");
    }

    public void setIsList(boolean isList) {
        this.isList = isList;
    }

    public boolean isList() {
        return this.isList;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Variable withValue(Object value) {
        setValue(value);
        return this;
    }
}
