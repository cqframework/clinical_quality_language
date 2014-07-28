package org.cqframework.cql.poc.translator.expressions;


/**
 * Created by bobd on 7/23/14.
 */
public class ComparisionExpression extends Expression{

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

    public ComparisionExpression(Expression left,Comparator comp, Expression right ){
        this.left = left;
        this.right=right;
        this.comp=comp;
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

}
