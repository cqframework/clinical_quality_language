package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.CqlList;

import java.util.ArrayList;
import java.util.Iterator;

/*
Mode(argument List<T>) T

The Mode operator returns the statistical mode of the elements in source.
If the source contains no non-null elements, null is returned.
If the source is null, the result is null.
*/

public class ModeEvaluator {

    public static Object mode(Object source, State state) {
        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {
            Iterable<?> element = (Iterable<?>)source;
            Iterator<?> itr = element.iterator();

            if (!itr.hasNext()) { // empty list
                return null;
            }

            ArrayList<Object> values = new ArrayList<>();
            while (itr.hasNext()) {
                Object value = itr.next();
                if (value != null) {
                    values.add(value);
                }
            }

            if (values.isEmpty()) { // all null
                return null;
            }

            values.sort(new CqlList().valueSort);

            int max = 0;
            Object mode = new Object();
            for (int i = 0; i < values.size(); ++i) {
                int count = 0;
                for (int j = i; j < values.size(); ++j) {
                    Boolean equal = EqualEvaluator.equal(values.get(i), values.get(j), state);
                    if (equal != null && equal) {
                        ++count;
                    }
                }
                if (count > max) {
                    mode = values.get(i);
                    max = count;
                }
            }
            return mode;
        }
        throw new InvalidOperatorArgument(
                "Mode(List<T>)",
                String.format("Mode(%s)", source.getClass().getName())
        );
    }

}