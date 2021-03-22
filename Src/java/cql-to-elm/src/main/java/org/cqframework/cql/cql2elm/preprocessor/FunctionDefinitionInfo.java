package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.Interval;
import org.cqframework.cql.gen.cqlParser;

public class FunctionDefinitionInfo extends BaseInfo {
    private String name;
    private String context;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getContext() { return context; }

    public void setContext(String value) { context = value; }

    @Override
    public cqlParser.FunctionDefinitionContext getDefinition() {
        return (cqlParser.FunctionDefinitionContext)super.getDefinition();
    }

    public void setDefinition(cqlParser.FunctionDefinitionContext value) {
        super.setDefinition(value);
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
