package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/23/14.
 */
public class OrExpression extends Expression{

    Expression left;
    Expression right;
    boolean xor = false;

    public OrExpression(Expression left, Expression right, boolean xor){
        this.left=left;
        this.right=right;
        this.xor = xor;
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

    public boolean isXor() {
        return xor;
    }

    public void setXor(boolean xor) {
        this.xor = xor;
    }
}
