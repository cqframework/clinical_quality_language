package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.Instance;
import org.opencds.cqf.cql.engine.execution.State;
public class InstanceEvaluator {

    public static Object internalEvaluate(Instance instance, State state, ElmLibraryVisitor<Object, State> visitor) {
        Object object = state.getEnvironment().createInstance(instance.getClassType());
        for (org.hl7.elm.r1.InstanceElement element : instance.getElement()) {
            Object value = visitor.visitExpression(element.getValue(), state);
            state.getEnvironment().setValue(object, element.getName(), value);
        }

        return object;
    }
}
