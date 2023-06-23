package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.execution.State;

import java.util.ArrayList;
import java.util.List;

public class ForEachEvaluator {

    public static Object forEach(Object source, Object element, State state) {
        if (source == null || element == null) {
            return null;
        }

        List<Object> retVal = new ArrayList<>();
        for (Object o : (Iterable<?>) source) {
            retVal.add(state.resolvePath(o, element.toString()));
        }
        return retVal;
    }

}
