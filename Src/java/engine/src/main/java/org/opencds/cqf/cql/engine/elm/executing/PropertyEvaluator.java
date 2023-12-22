package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.Property;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Tuple;

public class PropertyEvaluator {

    public static Object internalEvaluate(Property elm, State state, ElmLibraryVisitor<Object, State> visitor) {
        Object target = null;

        if (elm.getSource() != null) {
            target = visitor.visitExpression(elm.getSource(), state);
            // Tuple element access
            if (target instanceof Tuple) {
                // NOTE: translator will throw error if Tuple does not contain the specified element -- no need for
                // x.containsKey() check
                return ((Tuple) target).getElements().get(elm.getPath());
            }
        } else if (elm.getScope() != null) {
            target = state.resolveVariable(elm.getScope(), true).getValue();
        }

        if (target == null) {
            return null;
        }

        if (target instanceof Iterable) {}

        return state.getEnvironment().resolvePath(target, elm.getPath());
    }
}
