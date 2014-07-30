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
        super();
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

    @Override
    public Object evaluate(Context ctx) {
        Object left_val = left.evaluate(ctx);
        Object right_val = right.evaluate(ctx);
        String unit = null;
        try {
             unit = getCompatibleUnits(left_val, right_val);
        }catch(Exception e){
            return null;
        }
        Number l = getOperationValue(left_val);
        Number r = getOperationValue(right_val);
        if(r == null || l == null){
            return null;
        }
        Number ret = null;
        switch(op) {
            case ADD:
                ret = l.doubleValue()+r.doubleValue();
                break;
            case SUB:
                ret = l.doubleValue()-r.doubleValue();
                break;
            case MUL:
                ret = l.doubleValue()*r.doubleValue();
                break;
            case DIV:
                ret = l.doubleValue()/r.doubleValue();
                break;
            case MOD:
                ret = l.doubleValue()%r.doubleValue();
                break;
            case POW:
                ret = Math.pow(l.doubleValue(),r.doubleValue());
                break;
        }

        return new QuantityLiteral(ret,unit);
    }

    private String getCompatibleUnits(Object lv,Object rv)throws Exception{
       String lu= null;
        String ru = null;
       if(lv instanceof QuantityLiteral){
           lu = ((QuantityLiteral) lv).getUnit();
       }
        if(rv instanceof QuantityLiteral){
            ru = ((QuantityLiteral) rv).getUnit();
        }
       if(lu == null && ru == null){
           return null;
       }
       if(lu ==null || ru == null){
           return lu ==null ? ru : lu;
       }
       if(lu.equals(ru) ){
           return lu;
       }
       throw new Exception("");
    }

    private Number getOperationValue(Object o){
        if(o instanceof Number){
            return (Number)o;
        }else if(o instanceof QuantityLiteral){
            return ((QuantityLiteral) o).getQuantity();
        }
        return null;
    }

    public String toCql(){
        return "("+left.toCql()+" "+op.symbol+" "+right.toCql()+")";
    }
}
