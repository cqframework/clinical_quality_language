package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
*** STRING NOTES ***
[](argument String, index Integer) String

The indexer ([]) operator returns the character at the indexth position in a string.
Indexes in strings are defined to be 0-based.
If either argument is null, the result is null.
If the index is greater than the length of the string being indexed, the result is null.

*** LIST NOTES ***
[](argument List<T>, index Integer) T

The indexer ([]) operator returns the element at the indexth position in a list.
Indexes in lists are defined to be 0-based.
If the index is greater than the number of elements in the list, the result is null.
If either argument is null, the result is null.
*/

public class IndexerEvaluator {

    public static Object indexer(Object left, Object right) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof String) {
            if (right instanceof Integer) {
                if ((int) right < 0 || (int) right >= ((String) left).length()) {
                    return null;
                }

                return "" + ((String) left).charAt((int) right);
            }
        }

        if (left instanceof Iterable) {
            if (right instanceof Integer) {
                int index = -1;
                for (Object element : (Iterable<?>) left) {
                    index++;
                    if ((Integer) right == index) {
                        return element;
                    }
                }
                return null;
            }
        }

        throw new InvalidOperatorArgument(
                "Indexer(String, Integer) or Indexer(List<T>, Integer)",
                String.format(
                        "Indexer(%s, %s)",
                        left.getClass().getName(), right.getClass().getName()));
    }
}
