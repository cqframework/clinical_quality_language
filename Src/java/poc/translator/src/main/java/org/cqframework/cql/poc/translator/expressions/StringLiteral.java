package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/24/14.
 */
public class StringLiteral extends Expression{

    String value;

    public StringLiteral(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Context ctx) {
        return this.getValue();
    }
    @Override
    public String toCql() {
        return "'"+value+"'";
    }
}
