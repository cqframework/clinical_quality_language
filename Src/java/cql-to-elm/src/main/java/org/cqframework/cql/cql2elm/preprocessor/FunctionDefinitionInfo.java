package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.cql2elm.model.FunctionHeader;
import org.cqframework.cql.gen.cqlParser;

import java.util.Objects;
import java.util.StringJoiner;

public class FunctionDefinitionInfo extends BaseInfo {
    private String name;
    private String context;
    private FunctionHeader functionHeader;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public void setPreCompileOutput(FunctionHeader functionHeader) {
        this.functionHeader = functionHeader;
    }

    public FunctionHeader getPreCompileOutput() {
        return this.functionHeader;
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
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        FunctionDefinitionInfo that = (FunctionDefinitionInfo) other;
        return Objects.equals(name, that.name) && Objects.equals(context, that.context) && Objects.equals(functionHeader, that.functionHeader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, context, functionHeader);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FunctionDefinitionInfo.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("context='" + context + "'")
                .add("preCompileOutput=" + functionHeader)
                .toString();
    }
}
