package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class IntervalSelector extends Expression {

    Expression low;
    Expression high;

    public IntervalSelector(Expression low, Expression high) {
        super();
        this.low = low;
        this.high = high;
    }

    public Expression getLow() {
        return low;
    }

    public void setLow(Expression low) {
        this.low = low;
    }

    public Expression getHigh() {
        return high;
    }

    public void setHigh(Expression high) {
        this.high = high;
    }

    @Override
    public String toCql() {
        return "<"+low.toCql()+","+high.toCql()+">";
    }
}
