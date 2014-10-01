package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class ExpressionDefinitionInfo {
    private String name;
    private cqlParser.ExpressionDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.ExpressionDefinitionContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.ExpressionDefinitionContext value) {
        definition = value;
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
