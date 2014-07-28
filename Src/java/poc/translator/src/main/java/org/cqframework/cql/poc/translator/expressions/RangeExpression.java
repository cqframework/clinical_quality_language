package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/23/14.
 */
public class RangeExpression extends Expression{

    Expression value;
    Expression left;
    Expression right;

    boolean properly;
    public RangeExpression(Expression value, Expression left, Expression right, boolean properly) {
        this.value = value;
        this.left = left;
        this.right = right;
        this.properly = properly;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public boolean isProperly() {
        return properly;
    }

    public void setProperly(boolean properly) {
        this.properly = properly;
    }
}
