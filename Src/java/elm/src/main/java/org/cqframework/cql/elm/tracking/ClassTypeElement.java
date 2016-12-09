package org.cqframework.cql.elm.tracking;

public class ClassTypeElement {
    private String name;
    private DataType type;
    private boolean prohibited;
    private boolean oneBased;

    public ClassTypeElement(String name, DataType type, Boolean prohibited, Boolean oneBased) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("name");
        }

        if (type == null) {
            throw new IllegalArgumentException("type");
        }

        this.name = name;
        this.type = type;
        this.prohibited = prohibited != null ? prohibited : false;
        this.oneBased = oneBased != null ? oneBased : false;
    }

    public ClassTypeElement(String name, DataType type) {
        this(name, type, false, false);
    }

    public String getName() {
        return this.name;
    }

    public DataType getType() {
        return this.type;
    }

    public boolean isProhibited() {
        return prohibited;
    }

    public boolean isOneBased() {
        return oneBased;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassTypeElement)) {
            return false;
        }

        ClassTypeElement that = (ClassTypeElement) o;

        if (oneBased != that.oneBased) {
            return false;
        }
        if (prohibited != that.prohibited) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        if (!type.equals(that.type)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (prohibited ? 1 : 0);
        result = 31 * result + (oneBased ? 1 : 0);
        return result;
    }

    public boolean isSubTypeOf(ClassTypeElement that) {
        return this.getName().equals(that.getName()) && this.getType().isSubTypeOf(that.getType());
    }

    public boolean isSuperTypeOf(ClassTypeElement that) {
        return this.getName().equals(that.getName()) && this.getType().isSuperTypeOf(that.getType());
    }

    @Override
    public String toString() {
        return String.format("%s:%s%s", this.name, this.type.toString(), this.prohibited ? " (prohibited)" : "");
    }
}
