package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class CaseItem extends Expression {

    Expression when;
    Expression then;

    public CaseItem(Expression when, Expression then) {
        super();
        this.when = when;
        this.then = then;
    }

    public Expression getWhen() {
        return when;
    }

    public void setWhen(Expression when) {
        this.when = when;
    }

    public Expression getThen() {
        return then;
    }

    public void setThen(Expression then) {
        this.then = then;
    }

    @Override
    public String toCql() {
        return "when " + when.toCql() + " then " + then.toCql();
    }
}
