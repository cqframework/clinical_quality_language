package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlString

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
    fun indexer(left: CqlType?, right: CqlType?): CqlType? {
        if (left == null || right == null) {
            return null
        }

        if (left is String) {
            if (right is Integer) {
                if (right.value < 0 || right.value >= left.length) {
                    return null
                }

                return ("" + left.get(right.value)).toCqlString()
            }
        }

        if (left is List) {
            if (right is Integer) {
                var index = -1
                for (element in left) {
                    index++
                    if (right.value == index) {
                        return element
                    }
                }
                return null
            }
        }

        throw InvalidOperatorArgument(
            "Indexer(String, Integer) or Indexer(List<T>, Integer)",
            "Indexer(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
