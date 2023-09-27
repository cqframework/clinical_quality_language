package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.TypeSpecifier;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * POJO for the result of a pre compile operation (AKA: partial compile of function headers)
 */
public class PreCompileOutput {
    private final FunctionDef functionDef;
    private final TypeSpecifier resultType;

    public FunctionDef getFunctionDef() {
        return functionDef;
    }

    public TypeSpecifier getResultType() {
        return resultType;
    }

    public static PreCompileOutput noReturnType(FunctionDef functionDef) {
        return new PreCompileOutput(functionDef, null);
    }

    public static PreCompileOutput withReturnType(FunctionDef functionDef, TypeSpecifier resultType) {
        return new PreCompileOutput(functionDef, resultType);
    }

    private PreCompileOutput(FunctionDef functionDef, TypeSpecifier resultType) {
        this.functionDef = functionDef;
        this.resultType = resultType;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        PreCompileOutput that = (PreCompileOutput) other;
        return Objects.equals(functionDef, that.functionDef) && Objects.equals(resultType, that.resultType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionDef, resultType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PreCompileOutput.class.getSimpleName() + "[", "]")
                .add("functionDef=" + functionDef)
                .add("resultType=" + resultType)
                .toString();
    }
}
