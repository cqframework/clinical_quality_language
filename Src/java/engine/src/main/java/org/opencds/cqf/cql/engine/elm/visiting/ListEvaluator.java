package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.List;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.State;

import java.util.ArrayList;

public class ListEvaluator {

    public static Object internalEvaluate(List list, State state, CqlEngineVisitor visitor) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (Expression element : list.getElement()) {
            Object obj = visitor.validateOperand(visitor.visitExpression(element, state));
            result.add(obj);
        }
        return result;
    }
}
