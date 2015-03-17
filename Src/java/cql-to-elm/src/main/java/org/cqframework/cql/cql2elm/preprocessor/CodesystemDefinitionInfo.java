package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class CodesystemDefinitionInfo {
    private String name;
    private cqlParser.CodesystemDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.CodesystemDefinitionContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.CodesystemDefinitionContext value) {
        definition = value;
    }

    public CodesystemDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public CodesystemDefinitionInfo withDefinition(cqlParser.CodesystemDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
