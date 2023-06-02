package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;
import java.util.List;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
* The ELM Slice operation is the foundation for 3 CQL operators:
*
* Skip(argument List<T>, number Integer) List<T> -> X.skip(Y) -> Slice(X,Y,null)
* The Skip operator returns the elements in the list, skipping the first number of elements.
*   If it has less number elements, the result is empty.
* If the source list is null, the result is null.
* If the number of elements is null, the result is the entire list, no elements are skipped.
* If the number of elements is less than zero, the result is an empty list.
*
* Tail(argument List<T>) List<T> X.tail() -> Slice(X,1,null)
* The Tail operator returns all but the first element from the given list.
* If the list is empty, the result is empty.
* If the source list is null, the result is null.
*
* Take(argument List<T>, number Integer) List<T> X.take(Y) -> Slice(X,0,Y)
* The Take operator returns the first number of elements from the given list.
* If the list has less than number of elements, the result only contains the elements in the list.
* If the source list is null, the result is null.
* If the number is null, or 0 or less, the result is an empty list.
* */

public class SliceEvaluator extends org.cqframework.cql.elm.execution.Slice {

    public static Object slice(Object source, Integer start, Integer end) {
        if (source == null) {
            return null;
        }

        // Tricky part:
        // Take returns empty list -> Take(List<T>, null) -> start is 0 and end is null
        // Skip returns entire list -> Skip(List<T>, 0) -> start is 0 and end is null
        // Both have the same sig: Slice(List<T>, 0, null)
//        if (start == null) {
//            return source;
//        }

        if (source instanceof Iterable) {
            List<Object> ret = new ArrayList<>();

            if (end == null || end > ((List<?>) source).size()) {
                end = ((List<?>) source).size();
            }

            if (end < 0) {
                return ret;
            }

            for (; start < end; ++start) {
                ret.add(((List<?>) source).get(start));
            }

            return ret;
        }

        throw new InvalidOperatorArgument(
                "Slice(List<T>, Integer, Integer)",
                String.format("Slice(%s, %s, %s)", source.getClass().getName(), start.getClass().getName(), end.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);
        Integer start = (Integer) getStartIndex().evaluate(context);
        Integer end = getEndIndex() == null ? null : (Integer) getEndIndex().evaluate(context);

        return slice(source, start, end);
    }
}
