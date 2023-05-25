package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Instance;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceEvaluator {

    private static Logger logger = LoggerFactory.getLogger(InstanceEvaluator.class);

    public Object internalEvaluate(Instance instance, State state, CqlEngine visitor) {
        Object object = state.createInstance(instance.getClassType());
        for (org.hl7.elm.r1.InstanceElement element : instance.getElement()) {
            Object value = visitor.validateOperand(visitor.visitExpression(element.getValue(), state));
            state.setValue(object, element.getName(), value);
        }

        return object;
    }
}
