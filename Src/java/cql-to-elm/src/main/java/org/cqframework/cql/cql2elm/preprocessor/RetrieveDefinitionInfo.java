package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class RetrieveDefinitionInfo {
    private String name;
    private cqlParser.RetrieveDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.RetrieveDefinitionContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.RetrieveDefinitionContext value) {
        definition = value;
    }

    public RetrieveDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public RetrieveDefinitionInfo withDefinition(cqlParser.RetrieveDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
