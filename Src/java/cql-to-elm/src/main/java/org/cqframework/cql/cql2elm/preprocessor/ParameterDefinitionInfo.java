package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class ParameterDefinitionInfo {
    private String name;
    private cqlParser.ParameterDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.ParameterDefinitionContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.ParameterDefinitionContext value) {
        definition = value;
    }

    public ParameterDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public ParameterDefinitionInfo withDefinition(cqlParser.ParameterDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
