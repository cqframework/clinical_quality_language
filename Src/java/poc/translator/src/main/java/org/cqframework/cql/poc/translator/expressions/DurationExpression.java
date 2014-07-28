package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class DurationExpression {

    public enum Unit {

    }

    Expression expression;
    Unit unit;

    public DurationExpression(Expression expression, Unit unit) {
        this.expression = expression;
        this.unit = unit;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
