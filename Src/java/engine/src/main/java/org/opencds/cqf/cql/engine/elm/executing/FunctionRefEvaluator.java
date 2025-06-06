package org.opencds.cqf.cql.engine.elm.executing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cqframework.cql.elm.evaluating.SimpleElmEvaluator;
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionRefEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(FunctionRefEvaluator.class);

    public static Object internalEvaluate(
            FunctionRef functionRef, State state, ElmLibraryVisitor<Object, State> visitor) {
        ArrayList<Object> arguments = new ArrayList<>(functionRef.getOperand().size());
        for (Expression operand : functionRef.getOperand()) {
            arguments.add(visitor.visitExpression(operand, state));
        }

        boolean enteredLibrary = state.enterLibrary(functionRef.getLibraryName());
        try {
            FunctionDef functionDef = resolveOrCacheFunctionDef(state, functionRef, arguments);

            if (Boolean.TRUE.equals(functionDef.isExternal())) {
                return state.getEnvironment()
                        .getExternalFunctionProvider(state.getCurrentLibrary().getIdentifier())
                        .evaluate(functionDef.getName(), arguments);
            } else {
                // Establish activation frame with the function
                // definition being evaluated but without the start
                // time since the argument expressions will be
                // evaluated first.
                state.pushActivationFrame(functionDef, functionDef.getContext(), 0);
                try {
                    for (int i = 0; i < arguments.size(); i++) {
                        state.push(new Variable(functionDef.getOperand().get(i).getName()).withValue(arguments.get(i)));
                    }
                    // Set start time for the evaluation of the
                    // function body expression after the argument
                    // expressions have been evaluated.
                    state.getTopActivationFrame().setStartTime();
                    return visitor.visitExpression(functionDef.getExpression(), state);
                } finally {
                    state.popActivationFrame();
                }
            }
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }

    protected static FunctionDef resolveOrCacheFunctionDef(
            State state, FunctionRef functionRef, ArrayList<Object> arguments) {
        // We can cache a function ref if:
        // 1. ELM signatures are provided OR
        // 2. No arguments are provided (only one overload anyway)
        boolean eligibleForCaching = false;
        if (!functionRef.getSignature().isEmpty() || arguments.isEmpty()) {
            eligibleForCaching = true;
            if (state.getCache().getFunctionCache().containsKey(functionRef)) {
                return state.getCache().getFunctionCache().get(functionRef);
            }
        }

        FunctionDef functionDef = resolveFunctionRef(state, functionRef, arguments);

        if (eligibleForCaching) {
            state.getCache().getFunctionCache().put(functionRef, functionDef);
        }

        return functionDef;
    }

    protected static FunctionDef resolveFunctionRef(State state, FunctionRef functionRef, List<Object> arguments) {
        var name = functionRef.getName();
        var signature = functionRef.getSignature();

        var functionDefs = resolveFunctionRef(state, name, arguments, signature);

        return pickFunctionDef(state, name, arguments, signature, functionDefs);
    }

    static List<FunctionDef> resolveFunctionRef(
            State state, String name, List<Object> arguments, List<TypeSpecifier> signature) {
        var namedDefs = Libraries.getFunctionDefs(name, state.getCurrentLibrary());

        // If the function ref includes a signature, use the signature to find the matching function defs
        if (!signature.isEmpty()) {
            return namedDefs.stream()
                    .filter(x -> functionDefOperandsSignatureEqual(x, signature))
                    .collect(Collectors.toList());
        }

        logger.debug(
                "Using runtime function resolution for '{}'. It's recommended to always include signatures in ELM",
                name);

        return namedDefs.stream()
                .filter(x -> state.getEnvironment().matchesTypes(x, arguments))
                .collect(Collectors.toList());
    }

    static boolean functionDefOperandsSignatureEqual(FunctionDef functionDef, List<TypeSpecifier> signature) {
        var operands = functionDef.getOperand();

        // Check if the number of operands match and if the type specifiers match
        return operands.size() == signature.size()
                && IntStream.range(0, operands.size())
                        .allMatch(i -> operandDefTypeSpecifierEqual(operands.get(i), signature.get(i)));
    }

    static boolean operandDefTypeSpecifierEqual(OperandDef operandDef, TypeSpecifier typeSpecifier) {
        // An operand def can have an operandTypeSpecifier or operandType

        var operandDefOperandTypeSpecifier = operandDef.getOperandTypeSpecifier();
        if (operandDefOperandTypeSpecifier != null) {
            return SimpleElmEvaluator.typeSpecifiersEqual(operandDefOperandTypeSpecifier, typeSpecifier);
        }

        if (typeSpecifier instanceof NamedTypeSpecifier) {
            return SimpleElmEvaluator.qnamesEqual(
                    operandDef.getOperandType(), ((NamedTypeSpecifier) typeSpecifier).getName());
        }

        return false;
    }

    static FunctionDef pickFunctionDef(
            State state,
            String name,
            List<Object> arguments,
            List<TypeSpecifier> signature,
            List<FunctionDef> functionDefs) {
        var types = signature.isEmpty() ? arguments : signature;

        if (functionDefs.isEmpty()) {
            throw new CqlException(String.format(
                    "Could not resolve call to operator '%s(%s)' in library '%s'.",
                    name,
                    typesToString(state, types),
                    state.getCurrentLibrary().getIdentifier().getId()));
        }

        if (functionDefs.size() == 1) {
            // Normal case
            return functionDefs.get(0);
        }

        throw new CqlException(String.format(
                "Ambiguous call to operator '%s(%s)' in library '%s'.",
                name,
                typesToString(state, types),
                state.getCurrentLibrary().getIdentifier().getId()));
    }

    static String typesToString(State state, List<? extends Object> arguments) {
        StringBuilder argStr = new StringBuilder();
        if (arguments != null) {
            arguments.forEach(a -> {
                argStr.append((argStr.length() > 0) ? ", " : "");

                Class<?> type = state.getEnvironment().resolveType(a);
                argStr.append(type == null ? "null" : type.getTypeName());
            });
        }

        return argStr.toString();
    }
}
