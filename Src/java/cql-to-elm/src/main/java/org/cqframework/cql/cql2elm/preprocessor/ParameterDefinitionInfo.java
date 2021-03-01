package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.Interval;
import org.cqframework.cql.gen.cqlParser;

public class ParameterDefinitionInfo extends BaseInfo {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    @Override
    public cqlParser.ParameterDefinitionContext getDefinition() {
        return (cqlParser.ParameterDefinitionContext)super.getDefinition();
    }

    public void setDefinition(cqlParser.ParameterDefinitionContext value) {
        super.setDefinition(value);
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
