package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.cql2elm.PreCompileOutput;
import org.cqframework.cql.gen.cqlParser;

import java.util.Objects;
import java.util.StringJoiner;

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

    @Override
    public boolean equals(Object theO) {
        if (this == theO) return true;
        if (theO == null || getClass() != theO.getClass()) return false;
        FunctionDefinitionInfo that = (FunctionDefinitionInfo) theO;
        return Objects.equals(name, that.name) && Objects.equals(context, that.context) && Objects.equals(preCompileOutput, that.preCompileOutput);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, context, preCompileOutput);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FunctionDefinitionInfo.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("context='" + context + "'")
                .add("preCompileOutput=" + preCompileOutput)
                .toString();
    }
}
