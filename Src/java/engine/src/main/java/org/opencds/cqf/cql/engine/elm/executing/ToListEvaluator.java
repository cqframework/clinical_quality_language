package org.opencds.cqf.cql.engine.elm.executing;

import java.util.Collections;

public class ToListEvaluator {

    public static Object toList(Object operand)
    {
        // check to see if it is already a list
        if (operand instanceof Iterable)
        {
            return operand;
        }

        return operand == null ? Collections.emptyList() : Collections.singletonList(operand);
    }
}
