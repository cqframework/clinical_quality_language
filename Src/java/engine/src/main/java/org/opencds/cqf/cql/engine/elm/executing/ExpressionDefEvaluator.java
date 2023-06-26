package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.ExpressionResult;
import org.opencds.cqf.cql.engine.execution.State;

public class ExpressionDefEvaluator {
    public  static Object internalEvaluate(ExpressionDef expressionDef, State state, CqlEngine visitor) {
        if (expressionDef.getContext() != null) {
            state.enterContext(expressionDef.getContext());
        }
        try {
            state.pushEvaluatedResourceStack();
            VersionedIdentifier libraryId = state.getCurrentLibrary().getIdentifier();
            if (state.getCache().isExpressionCachingEnabled() && state.getCache().isExpressionCached(libraryId, expressionDef.getName())) {
                var er = state.getCache().getCachedExpression(libraryId, expressionDef.getName());
                state.getEvaluatedResources().addAll(er.evaluatedResources());
                return er.value();
            }

            Object value = visitor.visitExpression(expressionDef.getExpression(), state);

            if (state.getCache().isExpressionCachingEnabled()) {
                var er = new ExpressionResult(value, state.getEvaluatedResources());
                state.getCache().cacheExpression(libraryId, expressionDef.getName(), er);
            }

            return value;
        }
        finally {
            state.popEvaluatedResourceStack();
            if (expressionDef.getContext() != null) {
                state.exitContext();
            }
        }
    }
}
