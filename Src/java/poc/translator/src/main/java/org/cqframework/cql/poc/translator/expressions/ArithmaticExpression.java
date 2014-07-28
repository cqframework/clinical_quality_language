package org.cqframework.cql.poc.translator.expressions;

import java.math.BigDecimal;

/**
 * Created by bobd on 7/23/14.
 */
public class ArithmaticExpression extends Expression{
    public enum Operator {
        ADD("+"),
        SUB("-"),
        MUL ("*"),
        DIV ("/"),
        POW ("^"),
        MOD ("mod");

        private String symbol;
        private Operator(String s) {
            symbol = s;
        }

        public String symbol(){
            return this.symbol;
        }
        public static Operator bySymbol(String sym){
            Operator op = null;
            for (Operator operator : Operator.values()) {
                if(operator.symbol.equals(sym)){
                    op= operator;
                    break;
                }
            }
            return op;
        }
    }

    Expression left;
    Expression right;
    Operator op;

    public ArithmaticExpression(Expression left,Operator op, Expression right ){
        this.left = left;
        this.right=right;
        this.op=op;
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

    public Operator getOp() {
        return op;
    }

    public void setOp(Operator op) {
        this.op = op;
    }

    public String toCql(){
        return "("+left.toCql()+" "+op.symbol+" "+right.toCql()+")";
    }
}
