package org.opencds.cqf.cql.engine.elm.execution;


import java.util.ArrayList;
import java.util.List;

import org.opencds.cqf.cql.engine.execution.Context;

public class ForEachEvaluator extends org.cqframework.cql.elm.execution.ForEach {

    public Object forEach(Object source, Object element, Context context) {
        if (source == null || element == null) {
            return null;
        }

        List<Object> retVal = new ArrayList<>();
        for (Object o : (Iterable<?>) source) {
            retVal.add(context.resolvePath(o, element.toString()));
        }
        return retVal;
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        Object element = getSource().evaluate(context);

        return forEach(source, element, context);
    }
}
