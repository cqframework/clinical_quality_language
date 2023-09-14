package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.Interval;
import org.cqframework.cql.cql2elm.PreCompileOutput;
import org.cqframework.cql.gen.cqlParser;

public class FunctionDefinitionInfo extends BaseInfo {
    private String name;
    private String context;
    private PreCompileOutput preCompileOutput;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public void setPreCompileOutput(PreCompileOutput preCompileOutput) {
        this.preCompileOutput = preCompileOutput;
    }

    public PreCompileOutput getPreCompileOutput() {
        return this.preCompileOutput;
    }

    public String getContext() { return context; }

    public void setContext(String value) { context = value; }

    @Override
    public cqlParser.FunctionDefinitionContext getDefinition() {
        return (cqlParser.FunctionDefinitionContext)super.getDefinition();
    }

    public FunctionDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public FunctionDefinitionInfo withPreCompileOutput(PreCompileOutput value) {
        setPreCompileOutput(value);
        return this;
    }
}
