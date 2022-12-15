package org.opencds.cqf.cql.engine.elm.execution;

import org.apache.commons.lang3.NotImplementedException;
import org.opencds.cqf.cql.engine.debug.DebugAction;
import org.opencds.cqf.cql.engine.debug.SourceLocator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.Context;
public class Executable
{
    public Object evaluate(Context context) throws CqlException
    {
        try {
            DebugAction action = context.shouldDebug(this);
            Object result = internalEvaluate(context);
            if (action != DebugAction.NONE) {
                context.logDebugResult(this, result, action);
            }
            return result;
        }
        catch (Exception e) {
            if (e instanceof CqlException) {
                CqlException ce = (CqlException)e;
                if (ce.getSourceLocator() == null) {
                    ce.setSourceLocator(SourceLocator.fromNode(this, context.getCurrentLibrary()));
                    DebugAction action = context.shouldDebug(ce);
                    if (action != DebugAction.NONE) {
                        context.logDebugError(ce);
                    }
                }
                throw e;
            }
            else {
                CqlException ce = new CqlException(e, SourceLocator.fromNode(this, context.getCurrentLibrary()));
                DebugAction action = context.shouldDebug(ce);
                if (action != DebugAction.NONE) {
                    context.logDebugError(ce);
                }
                throw ce;
            }
        }
    }

    protected Object internalEvaluate(Context context) {
        throw new NotImplementedException(String.format("evaluate not implemented for class %s",
                this.getClass().getSimpleName()));
    }
}
