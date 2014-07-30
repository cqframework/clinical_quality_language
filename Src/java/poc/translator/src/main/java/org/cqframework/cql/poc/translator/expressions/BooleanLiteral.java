package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/24/14.
 */
public class BooleanLiteral extends Expression {

    boolean value;

    public BooleanLiteral(boolean value) {
        super();
        this.value = value;
    }

    public BooleanLiteral(String value) {

        this.value = Boolean.parseBoolean(value);
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public Object evaluate(Context ctx) {
        return this.getValue();
    }

    @Override
    public String toCql() {
        return Boolean.toString(value);
    }
}
