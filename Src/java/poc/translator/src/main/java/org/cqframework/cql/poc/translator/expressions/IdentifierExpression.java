package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/23/14.
 */
public class IdentifierExpression extends Expression{

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
}
