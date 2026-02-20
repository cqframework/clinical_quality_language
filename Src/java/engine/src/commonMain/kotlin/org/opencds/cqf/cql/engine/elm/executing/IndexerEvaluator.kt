package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

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
object IndexerEvaluator {
    @JvmStatic
    fun indexer(left: Any?, right: Any?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is String) {
            if (right is Int) {
                if (right < 0 || right >= left.length) {
                    return null
                }

                return "" + left.get(right)
            }
        }

        if (left is Iterable<*>) {
            if (right is Int) {
                var index = -1
                for (element in left) {
                    index++
                    if (right == index) {
                        return element
                    }
                }
                return null
            }
        }

        throw InvalidOperatorArgument(
            "Indexer(String, Integer) or Indexer(List<T>, Integer)",
            "Indexer(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
