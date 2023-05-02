package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Instance;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.State;

public class InstanceEvaluator {

    public static Object internalEvaluate(Instance instance, State state, CqlEngineVisitor visitor) {
        Object object = state.createInstance(instance.getClassType());
        for (org.hl7.elm.r1.InstanceElement element : instance.getElement()) {
            Object value = visitor.visitExpression(element.getValue(), state);
            state.setValue(object, element.getName(), value);
        }

        return object;
    }
}
