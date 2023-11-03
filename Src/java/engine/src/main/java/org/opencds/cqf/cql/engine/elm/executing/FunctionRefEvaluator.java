package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.TypeSpecifier;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionRefEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(FunctionRefEvaluator.class);

    public static Object internalEvaluate(FunctionRef functionRef, State state, ElmLibraryVisitor<Object,State> visitor) {
        ArrayList<Object> arguments = new ArrayList<>(functionRef.getOperand().size());
        for (Expression operand : functionRef.getOperand()) {
            arguments.add(visitor.visitExpression(operand, state));
        }

        boolean enteredLibrary = state.enterLibrary(functionRef.getLibraryName());
        try {
            FunctionDef functionDef = resolveOrCacheFunctionDef(state, functionRef, arguments);

            if (Boolean.TRUE.equals(functionDef.isExternal())) {
                return state.getEnvironment().getExternalFunctionProvider(state.getCurrentLibrary().getIdentifier()).evaluate(functionDef.getName(), arguments);
            } else {
                state.pushWindow();
                try {
                    for (int i = 0; i < arguments.size(); i++) {
                        state.push(new Variable().withName(functionDef.getOperand().get(i).getName()).withValue(arguments.get(i)));
                    }
                    return visitor.visitExpression(functionDef.getExpression(), state);
                } finally {
                    state.popWindow();
                }
            }
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }

    protected static FunctionDef resolveOrCacheFunctionDef(State state, FunctionRef functionRef, ArrayList<Object> arguments) {
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

        FunctionDef functionDef = resolveFunctionDef(state, functionRef, arguments);

        if (eligibleForCaching) {
            state.getCache().getFunctionCache().put(functionRef, functionDef);
        }

        return functionDef;
    }

    protected static FunctionDef resolveFunctionDef(State state, FunctionRef functionRef, ArrayList<Object> arguments) {
        return resolveFunctionRef(state, functionRef.getName(), arguments, functionRef.getSignature());
    }

    public static FunctionDef resolveFunctionRef(State state, final String name, final List<Object> arguments,
            final List<TypeSpecifier> signature) {
        FunctionDef ret;

        final List<? extends Object> types = signature.isEmpty() ? arguments : signature;

        ret = getResolvedFunctionDef(state, name, types, !signature.isEmpty());

        if (ret != null) {
            return ret;
        }

        throw new CqlException(String.format("Could not resolve call to operator '%s(%s)' in library '%s'.",
                name, getUnresolvedMessage(state, types, name), state.getCurrentLibrary().getIdentifier().getId()));
    }

    private static FunctionDef getResolvedFunctionDef(State state, final String name,
            final List<? extends Object> types, final boolean hasSignature) {
        var namedDefs = Libraries.getFunctionDefs(name, state.getCurrentLibrary());

        var candidateDefs = namedDefs
                .stream()
                .filter(x -> x.getOperand().size() == types.size())
                .collect(Collectors.toList());

        if (candidateDefs.size() == 1) {
            return candidateDefs.get(0);
        }

        if (candidateDefs.size() > 1 && !hasSignature) {
            logger.debug(
                    "Using runtime function resolution for '{}'. It's recommended to always include signatures in ELM",
                    name);
        }

        return candidateDefs.stream().filter(x -> state.getEnvironment().matchesTypes(x, types)).findFirst().orElse(null);
    }


    private static String getUnresolvedMessage(State state, List<? extends Object> arguments, String name) {
        StringBuilder argStr = new StringBuilder();
        if (arguments != null) {
            arguments.forEach(a -> argStr.append((argStr.length() > 0) ? ", " : "").append(state.getEnvironment().resolveType(a).getTypeName()));
        }

        return argStr.toString();
    }
}
