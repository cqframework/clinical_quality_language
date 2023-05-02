package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.execution.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
distinct(argument List<T>) List<T>

The distinct operator returns the given list with duplicates eliminated using equality semantics.

If the argument is null, the result is null.
*/

public class DistinctEvaluator
{

    public static List<Object> distinct(Iterable<?> source, State state)
    {
        if (source == null)
        {
            return null;
        }

        List<Object> result = new ArrayList<>();
        for (Object element : source)
        {
            if (element == null && result.parallelStream().noneMatch(Objects::isNull))
            {
                result.add(null);
                continue;
            }

            Object in = InEvaluator.in(element, result, null, state);

            if (in == null) continue;

            if (!(Boolean) in) result.add(element);
        }

        return result;
    }
}
