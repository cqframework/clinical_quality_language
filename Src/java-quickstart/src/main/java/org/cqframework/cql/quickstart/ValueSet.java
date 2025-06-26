package org.cqframework.cql.quickstart;

public class ValueSet {
    private final String id;

    public ValueSet(String id) {
        super();

        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueSet valueSet = (ValueSet) o;

        return id.equals(valueSet.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ValueSet{id='" + id + "'}";
    }
}
