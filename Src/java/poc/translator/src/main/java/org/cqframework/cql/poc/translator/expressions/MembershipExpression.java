package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class MembershipExpression extends Expression{

    public enum Membership{
        in,
        contains,
        like;
    }
    Expression left;
    Expression right;
    Membership membership;

    public MembershipExpression(Expression left, Expression right, Membership membership) {
        super();
        this.left = left;
        this.right = right;
        this.membership = membership;
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

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    @Override
    public String toCql() {
        return "("+left.toCql()+ " " +membership.name()+" "+right.toCql()+" )";
    }
}
