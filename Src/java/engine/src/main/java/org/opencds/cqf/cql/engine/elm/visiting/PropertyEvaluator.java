package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Property;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Tuple;

public class PropertyEvaluator {

    public static Object internalEvaluate(Property elm, State state, CqlEngineVisitor visitor) {
        Object target = null;

        if (elm.getSource() != null) {
            target = visitor.visitExpression(elm.getSource(), state);
            if (target instanceof ExpressionDef) {
                target = visitor.visitExpressionDef((ExpressionDef) target, state);
            }
            // Tuple element access
            if (target instanceof Tuple) {
              // NOTE: translator will throw error if Tuple does not contain the specified element -- no need for x.containsKey() check
              return ((Tuple)target).getElements().get(elm.getPath());
            }
        }
        else if (elm.getScope() != null) {
            target = state.resolveVariable(elm.getScope(), true).getValue();
        }

        if (target == null) {
            return null;
        }

        if (target instanceof Iterable) {

        }

        return state.resolvePath(target, elm.getPath());
    }
}