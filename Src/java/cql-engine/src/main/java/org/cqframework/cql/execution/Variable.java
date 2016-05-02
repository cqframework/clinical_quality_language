package org.cqframework.cql.execution;

/**
 * Created by Bryn on 5/1/2016.
 */
public class Variable {
    private String name;
    private Object value;

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
