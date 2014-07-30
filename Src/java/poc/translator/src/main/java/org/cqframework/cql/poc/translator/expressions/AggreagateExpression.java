package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class AggreagateExpression extends Expression{

    public enum Aggragator {
        distinct,
        collapse,
        expand;
    }

    Expression expression;
    AggreagateExpression.Aggragator aggragator;

    public AggreagateExpression(Expression expression, Aggragator aggragator) {
        super();
        this.expression = expression;
        this.aggragator = aggragator;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Aggragator getAggragator() {
        return aggragator;
    }

    public void setAggragator(Aggragator aggragator) {
        this.aggragator = aggragator;
    }

    @Override
    public String toCql() {
        return aggragator.name() +"("+expression.toCql()+")";
    }
}
