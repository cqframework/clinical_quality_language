package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class AnyInCodeSystemEvaluator extends org.cqframework.cql.elm.execution.AnyInCodeSystem {
    @Override
    protected Object internalEvaluate(Context context)
    {
        Object codes = this.getCodes().evaluate(context);
        Object cs = null;
        if (getCodesystem() != null) {
            cs = CodeSystemRefEvaluator.toCodeSystem(context, getCodesystem());
        }
        else if (getCodesystemExpression() != null) {
            cs = getCodesystemExpression().evaluate(context);
        }

        if (codes == null || cs == null) return null;

        if (codes instanceof Iterable)
        {
            Object result;
            for (Object code : (Iterable<?>) codes)
            {
                result = InCodeSystemEvaluator.inCodeSystem(context, code, cs);
                if (result instanceof Boolean && (Boolean) result)
                {
                    return true;
                }
            }
        }

        return false;
    }
}
