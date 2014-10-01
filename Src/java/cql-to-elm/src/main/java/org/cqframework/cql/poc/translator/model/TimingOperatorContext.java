package org.cqframework.cql.poc.translator.model;

import org.hl7.elm.r1.Expression;

public class TimingOperatorContext {
    private Expression left;
    private Expression right;

    public TimingOperatorContext() {
    }

    public TimingOperatorContext(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression value) {
        left = value;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression value) {
        right = value;
    }
}
