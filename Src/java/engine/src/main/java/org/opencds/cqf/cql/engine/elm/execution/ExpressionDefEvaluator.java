package org.opencds.cqf.cql.engine.elm.execution;

import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.ExpressionResult;

public class ExpressionDefEvaluator extends org.cqframework.cql.elm.execution.ExpressionDef {

    @Override
    protected Object internalEvaluate(Context context) {
        if (this.getContext() != null) {
            context.enterContext(this.getContext());
        }
        try {
            context.pushEvaluatedResourceStack();
            VersionedIdentifier libraryId = context.getCurrentLibrary().getIdentifier();
            if (context.isExpressionCachingEnabled() && context.isExpressionCached(libraryId, name)) {
                var er = context.getCachedExpression(libraryId, name);
                context.getEvaluatedResources().addAll(er.evaluatedResources());
                return er.value();
            }

            Object value = this.getExpression().evaluate(context);

            if (context.isExpressionCachingEnabled()) {
                var er = new ExpressionResult(value, context.getEvaluatedResources());
                context.cacheExpression(libraryId, name, er);
            }

            return value;

        } finally {
            context.popEvaluatedResourceStack();
            if (this.getContext() != null) {
                context.exitContext();
            }
        }
    }
}