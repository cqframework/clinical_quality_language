package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Instance;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceEvaluator {

    private static Logger logger = LoggerFactory.getLogger(InstanceEvaluator.class);

    public Object internalEvaluate(Instance instance, State state, CqlEngineVisitor visitor) {
        System.out.println("evaluating instance");
        Object object = state.createInstance(instance.getClassType());
        for (org.hl7.elm.r1.InstanceElement element : instance.getElement()) {
            Object value = visitor.visitExpression(element.getValue(), state);
            if (value instanceof ExpressionDef) {
                value = visitor.visitExpressionDef((ExpressionDef) value, state);
            }
            state.setValue(object, element.getName(), value);
        }

        return object;
    }
}
