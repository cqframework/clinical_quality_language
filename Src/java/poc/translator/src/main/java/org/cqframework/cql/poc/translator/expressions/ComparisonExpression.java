package org.cqframework.cql.poc.translator.expressions;


/**
 * Created by bobd on 7/23/14.
 */
public class ComparisonExpression extends Expression{

    public enum Comparator {
        EQ("="),
        NOT_EQ("<>"),
        LT( "<"),
        LT_EQ( "<="),
        GT (">"),
        GT_EQ ( ">=");

        private String symbol;
        private Comparator(String s) {
            symbol = s;
        }
        public String symbol(){
            return this.symbol;
        }

        public static Comparator bySymbol(String sym){
            Comparator op = null;
            for (Comparator operator : Comparator.values()) {
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
    Comparator comp;

    boolean isBooleanExpression = false;

    public ComparisonExpression(Expression left, Comparator comp, Expression right){
        this.left = left;
        this.right=right;
        this.comp=comp;
    }

    public ComparisonExpression(Expression left,  Comparator comp,Expression right, boolean isBooleanExpression) {
        this.left = left;
        this.right = right;
        this.comp = comp;
        this.isBooleanExpression = isBooleanExpression;
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

    public Comparator getComp() {
        return comp;
    }

    public void setComp(Comparator comp) {
        this.comp = comp;
    }

    public boolean isBooleanExpression() {
        return isBooleanExpression;
    }

    public void setBooleanExpression(boolean isBooleanExpression) {
        this.isBooleanExpression = isBooleanExpression;
    }

    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        buff.append(left.toCql());
        buff.append(" ");
        if(isBooleanExpression){
           buff.append("is");
           if(comp == Comparator.NOT_EQ){
               buff.append(" not");
           }
        }else {buff.append(comp.symbol());}
        buff.append(" ");
        buff.append(right.toCql());
        return buff.toString();
    }
}
