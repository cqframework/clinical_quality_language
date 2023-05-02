package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.OperandRef;
import org.opencds.cqf.cql.engine.execution.State;

public class OperandRefEvaluator {

    public static Object internalEvaluate(OperandRef operandRef, State state) {
        return state.resolveVariable(operandRef.getName(), true).getValue();
    }
}
