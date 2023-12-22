package org.hl7.cql.model;

public class TupleTypeElement {
    private String name;
    private DataType type;
    private boolean oneBased;

    public TupleTypeElement(String name, DataType type, boolean oneBased) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name");
        }

        if (type == null) {
            throw new IllegalArgumentException("type");
        }

        this.name = name;
        this.type = type;
        this.oneBased = oneBased;
    }

    public TupleTypeElement(String name, DataType type) {
        this(name, type, false);
    }

    public String getName() {
        return this.name;
    }

    public DataType getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        return (17 * this.name.hashCode()) + (33 * this.type.hashCode()) + (31 * (this.oneBased ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TupleTypeElement) {
            TupleTypeElement that = (TupleTypeElement) o;
            return this.name.equals(that.name) && this.type.equals(that.type) && (this.oneBased == that.oneBased);
        }

        return false;
    }

    public boolean isSubTypeOf(TupleTypeElement that) {
        return this.getName().equals(that.getName()) && this.getType().isSubTypeOf(that.getType());
    }

    public boolean isSuperTypeOf(TupleTypeElement that) {
        return this.getName().equals(that.getName()) && this.getType().isSuperTypeOf(that.getType());
    }

    @Override
    public String toString() {
        return String.format("%s:%s", this.name, this.type.toString());
    }

    public String toLabel() {
        return String.format("%s: %s", this.name, this.type.toLabel());
    }
}
