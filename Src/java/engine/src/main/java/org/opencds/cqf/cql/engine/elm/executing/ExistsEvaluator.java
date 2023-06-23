package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.runtime.CqlList;

/*
exists(argument List<T>) Boolean

The exists operator returns true if the list contains any non-null elements.
If the argument is null, the result is null.
*/

public class ExistsEvaluator {

    @SuppressWarnings("unchecked")
    public static Object exists(Object operand) {
        Iterable<Object> value = (Iterable<Object>) operand;

        if (value == null) {
            return false;
        }

        return !CqlList.toList(value, false).isEmpty();
    }

}
