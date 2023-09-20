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

    public static PreCompileOutput noReturnType(FunctionDef theFunctionDef) {
        return new PreCompileOutput(theFunctionDef, null);
    }

    public static PreCompileOutput withReturnType(FunctionDef theFunctionDef, TypeSpecifier theResultType) {
        return new PreCompileOutput(theFunctionDef, theResultType);
    }

    private PreCompileOutput(FunctionDef theFunctionDef, TypeSpecifier theResultType) {
        functionDef = theFunctionDef;
        resultType = theResultType;
    }

    @Override
    public boolean equals(Object theO) {
        if (this == theO) {
            return true;
        }
        if (theO == null || getClass() != theO.getClass()) {
            return false;
        }
        PreCompileOutput that = (PreCompileOutput) theO;
        return Objects.equals(functionDef, that.functionDef) && Objects.equals(resultType, that.resultType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionDef, resultType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PreCompileOutput.class.getSimpleName() + "[", "]")
                .add("myFunctionDef=" + functionDef)
                .add("myResultType=" + resultType)
                .toString();
    }
}
