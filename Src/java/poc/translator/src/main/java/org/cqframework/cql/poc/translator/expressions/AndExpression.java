package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/23/14.
 */
public class AndExpression extends Expression {

    Expression left;
    Expression right;

    public AndExpression(Expression left, Expression right){
        this.left=left;
        this.right=right;
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

    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        if(left instanceof  OrExpression){
            buff.append("(");
            buff.append(left.toCql());
            buff.append(")");
        }else{buff.append(left.toCql());}
        buff.append(" and ");
        if(right instanceof  OrExpression){
           buff.append("(");
           buff.append(right.toCql());
           buff.append(")");
        }else{buff.append(right.toCql());}
        return buff.toString();
    }
}
