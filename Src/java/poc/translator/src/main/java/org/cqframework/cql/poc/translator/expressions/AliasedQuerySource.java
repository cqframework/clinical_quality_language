package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/23/14.
 */
public class AliasedQuerySource extends Expression{

    Expression querySource;
    String alias;

    public AliasedQuerySource(Expression querySource, String alias) {
        this.querySource = querySource;
        this.alias = alias;
    }

    public Expression getQuerySource() {
        return querySource;
    }

    public void setQuerySource(Expression querySource) {
        this.querySource = querySource;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toCql() {
        return querySource.toCql()+ " " +alias;
    }
}
