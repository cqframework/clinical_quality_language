package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class InstanceEvaluator extends org.cqframework.cql.elm.execution.Instance {

    @Override
    protected Object internalEvaluate(Context context) {
        Object object = context.createInstance(this.getClassType());
        for (org.cqframework.cql.elm.execution.InstanceElement element : this.getElement()) {
            Object value = element.getValue().evaluate(context);
            context.setValue(object, element.getName(), value);
        }

        return object;
    }
}
