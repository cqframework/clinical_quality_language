package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/25/14.
 */
public class LetStatement extends Statement{

    String identifier;
    Expression expression;

    public LetStatement(String identifier, Expression expression){
        super();
        this.expression=expression;
        this.identifier=identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate(Context ctx) {
        return expression.evaluate(ctx);
    }

    @Override
    public String toCql() {
        return "let "+identifier+" = "+expression.toCql();
    }
}
