package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class PredecessorExpression extends Expression {

    Expression expression;

    public PredecessorExpression(Expression expression) {
        super();
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toCql() {
        return "predecessor of "+expression.toCql();
    }
}
