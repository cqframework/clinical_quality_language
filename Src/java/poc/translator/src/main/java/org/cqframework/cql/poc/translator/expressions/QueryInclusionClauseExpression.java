package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/24/14.
 */
public class QueryInclusionClauseExpression extends Expression {

    AliasedQuerySource aliasedQuerySource;
    Expression expression;
    boolean negated;

    public QueryInclusionClauseExpression(AliasedQuerySource aliasedQuerySource, Expression expression, boolean negated) {
        super();
        this.aliasedQuerySource = aliasedQuerySource;
        this.expression = expression;
        this.negated = negated;
    }

    public AliasedQuerySource getAliasedQuerySource() {
        return aliasedQuerySource;
    }

    public void setAliasedQuerySource(AliasedQuerySource aliasedQuerySource) {
        this.aliasedQuerySource = aliasedQuerySource;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        if (negated) {
            buff.append("without ");
        } else {
            buff.append("with ");
        }

        buff.append(aliasedQuerySource.toCql());
        buff.append(" ");
        if (expression != null) {
            buff.append(expression.toCql());
        }
        return buff.toString();
    }
}
