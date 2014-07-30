package org.cqframework.cql.poc.translator.expressions;

public class QualifiedIdentifier extends Expression {

    String qualifier;
    String identifier;
    boolean valuesetIdentifier;

    public QualifiedIdentifier(String qualifier, String identifier, boolean valuesetIdentifier) {
        super();
        this.qualifier = qualifier;
        this.identifier = identifier;
        this.valuesetIdentifier = valuesetIdentifier;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isValuesetIdentifier() {
        return valuesetIdentifier;
    }

    public void setValuesetIdentifier(boolean valuesetIdentifier) {
        this.valuesetIdentifier = valuesetIdentifier;
    }

    @Override

    public Object evaluate(Context ctx) {
        return ctx.get(this);
    }

    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        if (qualifier != null) {
            buff.append(qualifier);
            buff.append(".");
        }
        if (isValuesetIdentifier()) {
            buff.append("\"" + identifier + "\"");
        } else {
            buff.append(identifier);
        }
        return buff.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QualifiedIdentifier that = (QualifiedIdentifier) o;

        if (valuesetIdentifier != that.valuesetIdentifier) return false;
        if (!identifier.equals(that.identifier)) return false;
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qualifier != null ? qualifier.hashCode() : 0;
        result = 31 * result + identifier.hashCode();
        result = 31 * result + (valuesetIdentifier ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QualifiedIdentifier{" +
                "qualifier='" + qualifier + '\'' +
                ", identifier='" + identifier + '\'' +
                ", valuesetIdentifier=" + valuesetIdentifier +
                '}';
    }
}
