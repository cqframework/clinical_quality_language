package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

/**
 * Created by Bryn on 5/22/2016.
 */
public class CodeDefinitionInfo extends BaseInfo {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public CodeDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    @Override
    public cqlParser.CodeDefinitionContext getDefinition() {
        return (cqlParser.CodeDefinitionContext) super.getDefinition();
    }

    public void setDefinition(cqlParser.CodeDefinitionContext value) {
        super.setDefinition(value);
    }

    public CodeDefinitionInfo withDefinition(cqlParser.CodeDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
