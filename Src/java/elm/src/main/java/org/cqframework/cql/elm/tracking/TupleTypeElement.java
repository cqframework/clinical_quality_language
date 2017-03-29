package org.cqframework.cql.elm.tracking;

public class TupleTypeElement {
    private String name;
    private DataType type;

    public TupleTypeElement(String name, DataType type) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name");
        }

        if (type == null) {
            throw new IllegalArgumentException("type");
        }

        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public DataType getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        return (17 * this.name.hashCode()) + (33 * this.type.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TupleTypeElement) {
            TupleTypeElement that = (TupleTypeElement)o;
            return this.name.equals(that.name) && this.type.equals(that.type);
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
