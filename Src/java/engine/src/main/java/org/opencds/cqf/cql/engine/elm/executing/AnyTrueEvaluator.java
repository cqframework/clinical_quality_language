package org.opencds.cqf.cql.engine.elm.executing;

import java.util.Iterator;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
AnyTrue(argument List<Boolean>) Boolean

The AnyTrue operator returns true if any non-null element in the source is true.
If the source contains no non-null elements, false is returned.
If the source is null, the result is null.
*/

public class AnyTrueEvaluator {

    public static Boolean anyTrue(Object src) {
        if (src == null) {
            return false;
        }

        if (src instanceof Iterable) {
            Iterable<?> element = (Iterable<?>) src;
            Iterator<?> elemsItr = element.iterator();

            if (!elemsItr.hasNext()) { // empty list
                return false;
            }

            while (elemsItr.hasNext()) {
                Object exp = elemsItr.next();

                if (exp == null) { // skip null
                    continue;
                }

                if (exp instanceof Boolean) {
                    Boolean boolVal = (Boolean) exp;

                    if (Boolean.TRUE == boolVal) {
                        return true;
                    }
                } else {
                    throw new InvalidOperatorArgument(
                            "AnyTrue(List<Boolean>)",
                            String.format("AnyTrue(List<%s>)", exp.getClass().getName()));
                }
            }

            return false; // all null or all false
        }

        throw new InvalidOperatorArgument(
                "AnyTrue(List<Boolean>)",
                String.format("AnyTrue(%s)", src.getClass().getName()));
    }
}
