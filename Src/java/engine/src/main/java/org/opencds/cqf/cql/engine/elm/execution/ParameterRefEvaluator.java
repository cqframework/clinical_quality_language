package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class ParameterRefEvaluator extends org.cqframework.cql.elm.execution.ParameterRef {

    @Override
    protected Object internalEvaluate(Context context) {
        return context.resolveParameterRef(this.getLibraryName(), this.getName());
    }
}
