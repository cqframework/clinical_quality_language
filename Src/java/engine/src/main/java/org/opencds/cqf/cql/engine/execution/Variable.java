package org.opencds.cqf.cql.engine.execution;

public class Variable {
    private String name;
    private Object value;
    // for AliasEvaluator
    private boolean isList;

    public void setIsList (boolean isList) {
        this.isList = isList;
    }

    public boolean isList () {
        return this.isList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Variable withName(String name) {
        setName(name);
        return this;
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
