package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class CodesystemDefinitionInfo extends BaseInfo {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    @Override
    public cqlParser.CodesystemDefinitionContext getDefinition() {
        return (cqlParser.CodesystemDefinitionContext) super.getDefinition();
    }

    public void setDefinition(cqlParser.CodesystemDefinitionContext value) {
        super.setDefinition(value);
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
