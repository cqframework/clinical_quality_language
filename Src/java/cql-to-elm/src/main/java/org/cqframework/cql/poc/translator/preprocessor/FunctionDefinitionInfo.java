package org.cqframework.cql.poc.translator.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class FunctionDefinitionInfo {
    private String name;
    private cqlParser.FunctionDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.FunctionDefinitionContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.FunctionDefinitionContext value) {
        definition = value;
    }

    public FunctionDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public FunctionDefinitionInfo withDefinition(cqlParser.FunctionDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
