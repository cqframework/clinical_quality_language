package org.cqframework.cql.cql2elm.model;

import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.OperandDef;
import org.hl7.elm.r1.TypeSpecifier;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * POJO for the result of a pre compile operation (AKA: partial compile of function headers)
 */
public class FunctionHeader {
    private final FunctionDef functionDef;
    private final TypeSpecifier resultType;

    private boolean isCompiled = false;

    public FunctionDef getFunctionDef() {
        return functionDef;
    }

    public TypeSpecifier getResultType() {
        return resultType;
    }

    public static FunctionHeader noReturnType(FunctionDef functionDef) {
        return new FunctionHeader(functionDef, null);
    }

    public static FunctionHeader withReturnType(FunctionDef functionDef, TypeSpecifier resultType) {
        return new FunctionHeader(functionDef, resultType);
    }

    private FunctionHeader(FunctionDef functionDef, TypeSpecifier resultType) {
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
        FunctionHeader that = (FunctionHeader) other;
        return Objects.equals(functionDef, that.functionDef) && Objects.equals(resultType, that.resultType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionDef, resultType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FunctionHeader.class.getSimpleName() + "[", "]")
                .add("functionDef=" + functionDef)
                .add("resultType=" + resultType)
                .toString();
    }

    public String getMangledName() {
        StringBuilder sb = new StringBuilder();
        sb.append(functionDef.getName());
        sb.append("_");
        for (OperandDef od : functionDef.getOperand()) {
            sb.append(od.getOperandTypeSpecifier() != null ? od.getOperandTypeSpecifier().toString() : "void");
        }
        sb.append("_");
        return sb.toString();
    }

    public boolean getIsCompiled() {
        return isCompiled;
    }

    public void setIsCompiled() {
        isCompiled = true;
    }
}
