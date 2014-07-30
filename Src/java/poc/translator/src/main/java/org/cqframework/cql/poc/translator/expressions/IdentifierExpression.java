package org.cqframework.cql.poc.translator.expressions;

public class IdentifierExpression extends Expression {

    String identifier;

    public IdentifierExpression(String identifier) {

        super();
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Object evaluate(Context ctx) {
        return ctx.get(this);
    }

    @Override
    public String toCql() {
        return identifier;
    }

    public boolean equals(Object o) {
        return o != null && o instanceof IdentifierExpression && identifier.equals(((IdentifierExpression) o).getIdentifier());
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "IdentifierExpression{" +
                "identifier='" + identifier + '\'' +
                '}';

    }
}
