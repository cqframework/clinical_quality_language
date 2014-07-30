package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class InFixExpression extends Expression {

    public enum Operator {
        union,
        intersect,
        except;
    }

    Expression left;
    Expression right;
    Operator operator;

    public InFixExpression(Expression left, Expression right, Operator operator) {
        super();
        this.left = left;
        this.right = right;
        this.operator = operator;
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

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toCql() {
        return "(" + left.toCql() + " " + operator.name() + " " + right.toCql() + ")";
    }
}
