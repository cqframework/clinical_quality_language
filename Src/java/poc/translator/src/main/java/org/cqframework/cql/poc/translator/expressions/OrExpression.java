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

    public String conjuntionString(){
        return isXor()? "xor" : "or";
    }
    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        if((left instanceof  OrExpression && ((OrExpression) left).isXor() != isXor()) ||
            left instanceof AndExpression){
            buff.append("(");
            buff.append(left.toCql());
            buff.append(")");
        }else{buff.append(left.toCql());}
        buff.append(" ");
        buff.append(conjuntionString());
        buff.append(" ");
        if((right instanceof  OrExpression && ((OrExpression) right).isXor() != isXor()) ||
                right instanceof AndExpression){
            buff.append("(");
            buff.append(right.toCql());
            buff.append(")");
        }else{buff.append(right.toCql());}
        return buff.toString();
    }
}
