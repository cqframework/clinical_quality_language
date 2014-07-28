package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class SuccessorExpression extends Expression {

    Expression expression;

    public SuccessorExpression(Expression expression) {
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
        return "successor of "+expression.toCql();
    }
}
