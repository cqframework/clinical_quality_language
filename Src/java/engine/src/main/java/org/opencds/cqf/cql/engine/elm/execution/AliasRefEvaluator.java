package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class AliasRefEvaluator extends org.cqframework.cql.elm.execution.AliasRef {

    @Override
    protected Object internalEvaluate(Context context) {
        return context.resolveAlias(this.getName());
    }
}
