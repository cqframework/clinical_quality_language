package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class OperandRefEvaluator extends org.cqframework.cql.elm.execution.OperandRef {

    @Override
    protected Object internalEvaluate(Context context) {
        return context.resolveVariable(this.getName(), true).getValue();
    }
}
