package org.cqframework.cql.poc.translator.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class LetStatementInfo {
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

    public LetStatementInfo withName(String value) {
        setName(value);
        return this;
    }

    public LetStatementInfo withDefinition(cqlParser.ExpressionDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
