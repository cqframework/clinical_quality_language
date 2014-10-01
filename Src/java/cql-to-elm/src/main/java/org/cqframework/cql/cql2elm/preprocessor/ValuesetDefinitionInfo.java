package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class ValuesetDefinitionInfo {
    private String name;
    private cqlParser.ValuesetDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.ValuesetDefinitionContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.ValuesetDefinitionContext value) {
        definition = value;
    }

    public ValuesetDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public ValuesetDefinitionInfo withDefinition(cqlParser.ValuesetDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
