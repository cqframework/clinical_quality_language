package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class ExpressionRefEvaluator extends org.cqframework.cql.elm.execution.ExpressionRef {

    @Override
    protected Object internalEvaluate(Context context) {
        boolean enteredLibrary = context.enterLibrary(this.getLibraryName());
        try {
            return context.resolveExpressionRef(this.getName()).evaluate(context);
        }
        finally {
            context.exitLibrary(enteredLibrary);
        }
    }
}
