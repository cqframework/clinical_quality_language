package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class FunctionDefinitionInfo {
    private String name;
    private String context;
    private cqlParser.FunctionDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getContext() { return context; }

    public void setContext(String value) { context = value; }

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
