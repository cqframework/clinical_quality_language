package org.opencds.cqf.cql.engine.elm.executing;

import java.util.ArrayList;
import java.util.List;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
flatten(argument List<List<T>>) List<T>

The flatten operator flattens a list of lists into a single list.
*/

public class FlattenEvaluator {

    public static List<Object> flatten(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Iterable) {
            List<Object> resultList = new ArrayList<>();
            for (Object element : (Iterable<?>) operand) {
                if (element == null) {
                    resultList.add(null);
                } else {
                    for (Object subElement : (Iterable<?>) element) {
                        resultList.add(subElement);
                    }
                }
            }

            return resultList;
        }

        throw new InvalidOperatorArgument(
                "Flatten(List<List<T>>)",
                String.format("Flatten(%s)", operand.getClass().getName()));
    }
}
