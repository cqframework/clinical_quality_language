package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

/*
 * The ELM Slice operation is the foundation for 3 CQL operators:
 *
 * Slice(source List<T>, startIndex Integer, endIndex Integer) List<T>
 * The Slice operator returns a portion of the elements in a list, beginning at the start index and
 *   ending just before the ending index.
 * If the source list is null, the result is null.
 * If the startIndex is null, the slice begins at the first element of the list.
 * If the endIndex is null, the slice continues to the last element of the list.
 * If the startIndex or endIndex is less than 0, or if the endIndex is less than the startIndex, the
 *   result is an empty list.
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
object SliceEvaluator {
    @JvmStatic
    fun slice(source: Value?, start: Value?, end: Value?): List? {

        if (source == null) {
            return null
        }

        // Tricky part:
        // Take returns empty list -> Take(List<T>, null) -> start is 0 and end is null
        // Skip returns entire list -> Skip(List<T>, 0) -> start is 0 and end is null
        // Both have the same sig: Slice(List<T>, 0, null)
        //        if (start == null) {
        //            return source;
        //        }
        if (source is List && start is Integer? && end is Integer?) {
            // A null start index begins at the first element, so Skip(X, null) is the entire list.
            var start = start?.value ?: 0
            var end = end?.value
            val ret = mutableListOf<Value?>()

            if (end == null || end > source.count()) {
                end = source.count()
            }

            // A negative start or end index gives an empty list, so Skip(X, -1) is { }.
            if (start < 0 || end < 0) {
                return ret.toCqlList()
            }

            while (start < end) {
                ret.add(source.elementAt(start))
                ++start
            }

            return ret.toCqlList()
        }

        throw InvalidOperatorArgument(
            "Slice(List<T>, Integer, Integer)",
            "Slice(${source.typeAsString}, ${start?.typeAsString}, ${end?.typeAsString})",
        )
    }
}
