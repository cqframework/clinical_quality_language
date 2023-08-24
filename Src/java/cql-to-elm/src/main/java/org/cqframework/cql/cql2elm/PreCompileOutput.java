package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.OperandDef;
import org.hl7.elm.r1.TypeSpecifier;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

// TODO: javadoc
public class PreCompileOutput {
    private final FunctionDef myFunctionDef;
    private final TypeSpecifier myResultType;
    private final String myHash;

    public FunctionDef getFunctionDef() {
        return myFunctionDef;
    }

    public TypeSpecifier getResultType() {
        return myResultType;
    }

    public static PreCompileOutput noReturnType(FunctionDef theFunctionDef) {
        return new PreCompileOutput(theFunctionDef, null);
    }

    public static PreCompileOutput withReturnType(FunctionDef theFunctionDef, TypeSpecifier theResultType) {
        return new PreCompileOutput(theFunctionDef, theResultType);
    }

    private PreCompileOutput(FunctionDef theFunctionDef, TypeSpecifier theResultType) {
        myFunctionDef = theFunctionDef;
        myResultType = theResultType;
        myHash = generateHash();
    }

    public String generateHash() {
//            hash ~= fun.name() + combine(operands.stream().map(o -> o.getType().text()), ",")
        final List<OperandDef> operand = myFunctionDef.getOperand();

        // TODO:  operand type is null
        final String commaDelimitedParamTypes = operand.stream()
                .map(OperandDef::getResultType)
                .map(Object::toString)
                .collect(Collectors.joining(","));

        return myFunctionDef.getName() + ": " + commaDelimitedParamTypes;
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
        return Objects.equals(myFunctionDef, that.myFunctionDef) && Objects.equals(myResultType, that.myResultType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myFunctionDef, myResultType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PreCompileOutput.class.getSimpleName() + "[", "]")
                .add("myFunctionDef=" + myFunctionDef)
                .add("myResultType=" + myResultType)
                .toString();
    }
}
