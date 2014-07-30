package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/23/14.
 */
public class ExistenceExpression extends Expression {

    boolean negated = false;
    Expression expression;

    public ExistenceExpression(boolean negated, Expression exp) {
        super();
        this.negated = negated;
        this.expression = exp;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate(Context ctx) {
        Object val = expression.evaluate(ctx);
        return isNegated() ? val == null : val != null;
    }

    @Override
    public String toCql() {
        String ex = negated ? "not" : "exists";
        return ex + " " + expression.toCql();
    }
}
