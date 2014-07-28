package org.cqframework.cql.poc.translator.expressions;

import org.cqframework.cql.poc.translator.Context;

/**
 * Created by bobd on 7/23/14.
 */
public abstract class Expression {

    String expression_identifier;

    public String getExpression_identifier() {
        return expression_identifier;
    }

    public void setExpression_identifier(String expression_identifier) {
        this.expression_identifier = expression_identifier;
    }

}
