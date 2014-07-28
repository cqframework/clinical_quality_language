package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class AccessorExpression extends Expression {

    Expression expression;
    String identifier;
    boolean valuesetAccessor = false;

    public AccessorExpression(Expression expression, String identifier, boolean isValuesetAccessor) {
        this.expression = expression;
        this.identifier = identifier;
        this.valuesetAccessor=isValuesetAccessor;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isValuesetAccessor() {
        return valuesetAccessor;
    }

    public void setValuesetAccessor(boolean valuesetAccessor) {
        this.valuesetAccessor = valuesetAccessor;
    }
}
