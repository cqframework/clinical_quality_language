package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class ExpressionDefinitionInfo extends BaseInfo {
    private String name;
    private String context;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String value) {
        context = value;
    }

    @Override
    public cqlParser.ExpressionDefinitionContext getDefinition() {
        return (cqlParser.ExpressionDefinitionContext) super.getDefinition();
    }

    public void setDefinition(cqlParser.ExpressionDefinitionContext value) {
        super.setDefinition(value);
    }

    public ExpressionDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public ExpressionDefinitionInfo withDefinition(cqlParser.ExpressionDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
