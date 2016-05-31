package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

/**
 * Created by Bryn on 5/22/2016.
 */
public class CodeDefinitionInfo {
    private String name;
    private cqlParser.CodeDefinitionContext definition;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public cqlParser.CodeDefinitionContext getDefinition() {
        return definition;
    }

    public void setDefinition(cqlParser.CodeDefinitionContext value) {
        definition = value;
    }

    public CodeDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public CodeDefinitionInfo withDefinition(cqlParser.CodeDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
