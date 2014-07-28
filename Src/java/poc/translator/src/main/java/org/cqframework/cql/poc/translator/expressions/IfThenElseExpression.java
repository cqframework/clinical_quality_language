package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/23/14.
 */
public class IfThenElseExpression extends Expression{

    Expression _if;
    Expression _else;
    Expression _then;

    public IfThenElseExpression(Expression _if, Expression _then, Expression _else){
        this._if=_if;
        this._then = _then;
        this._else = _else;
    }

    public Expression getIf() {
        return _if;
    }

    public void setIf(Expression _if) {
        this._if = _if;
    }

    public Expression getElse() {
        return _else;
    }

    public void setElse(Expression _else) {
        this._else = _else;
    }

    public Expression getThen() {
        return _then;
    }

    public void setThen(Expression _then) {
        this._then = _then;
    }
}
