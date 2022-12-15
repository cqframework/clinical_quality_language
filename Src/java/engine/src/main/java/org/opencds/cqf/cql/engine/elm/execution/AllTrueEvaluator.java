package org.opencds.cqf.cql.engine.elm.execution;

import java.util.Iterator;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
AllTrue(argument List<Boolean>) Boolean

The AllTrue operator returns true if all the non-null elements in the source are true.
If the source contains no non-null elements, true is returned.
If the source is null, the result is null.
*/

public class AllTrueEvaluator extends org.cqframework.cql.elm.execution.AllTrue {

    public static Boolean allTrue(Object src) {
        if (src == null) {
            return null;
        }

        if (src instanceof Iterable) {
            Iterable<?> element = (Iterable<?>)src;
            Iterator<?> elemsItr = element.iterator();

            if (!elemsItr.hasNext()) { // empty list
                return true;
            }

            while (elemsItr.hasNext()) {
                Object exp = elemsItr.next();

                if (exp == null) { // skip null
                    continue;
                }

                if (exp instanceof Boolean) {
                    Boolean boolVal = (Boolean) exp;

                    if (!boolVal) {
                        return false;
                    }
                }
                else {
                    throw new InvalidOperatorArgument("AllTrue(List<Boolean>)", String.format("AllTrue(List<%s>)", exp.getClass().getName()));
                }
            }
            return true;
        }

        throw new InvalidOperatorArgument("AllTrue(List<Boolean>)", String.format("AllTrue(%s)", src.getClass().getName()));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object src = getSource().evaluate(context);
        return allTrue(src);
    }
}
