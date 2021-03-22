package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.Interval;
import org.cqframework.cql.gen.cqlParser;

public class ValuesetDefinitionInfo extends BaseInfo {
    private String name;
    private String header;
    private Interval headerInterval;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    @Override
    public cqlParser.ValuesetDefinitionContext getDefinition() {
        return (cqlParser.ValuesetDefinitionContext)super.getDefinition();
    }

    public void setDefinition(cqlParser.ValuesetDefinitionContext value) {
        super.setDefinition(value);
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
