package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class TimeBoundaryExpression {

    public enum Boundary{
        start,
        end;
    }

    Expression expression;
    Boundary boundary;

    public TimeBoundaryExpression(Expression expression, Boundary boundary) {
        this.expression = expression;
        this.boundary = boundary;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Boundary getBoundary() {
        return boundary;
    }

    public void setBoundary(Boundary boundary) {
        this.boundary = boundary;
    }


}
