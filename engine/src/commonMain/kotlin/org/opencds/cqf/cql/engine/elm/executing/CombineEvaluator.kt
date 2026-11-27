package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlString

/*
Combine(source List<String>) String
Combine(source List<String>, separator String) String

The Combine operator combines a list of strings, optionally separating each string with the given separator.
If the source argument is null, or the source list is empty, the result is null.
If the separator argument is null, it is ignored.
For consistency with aggregate operator behavior, null elements in the input list are ignored.
*/
object CombineEvaluator {
    @JvmStatic
    fun combine(source: Value?, separator: Value?): String? {
        if (source == null) {
            return null
        }

        // A null separator is ignored, the same as no separator.
        val separator = separator ?: String.EMPTY_STRING

        if (source is List && separator is String) {
            val buffer = StringBuilder("")
            val iterator = source.iterator()
            var first = true

            while (iterator.hasNext()) {
                val item = iterator.next()

                // Null elements are ignored.
                if (item == null) {
                    continue
                }

                if (item is String) {
                    if (!first) {
                        buffer.append(separator.value)
                    } else {
                        first = false
                    }
                    buffer.append(item.value)
                } else {
                    throw InvalidOperatorArgument(
                        "Combine(List<String>) or Combine(List<String>, String)",
                        "Combine(List<${item.typeAsString}>${if (separator.value == "") "" else ", " + separator})",
                    )
                }
            }
            // Combine of an empty list, or of a list whose elements are all null, is null (first is
            // still true iff no elements were appended).
            return if (first) null else buffer.toString().toCqlString()
        }

        throw InvalidOperatorArgument(
            "Combine(List<String>) or Combine(List<String>, String)",
            "Combine(${source.typeAsString}, ${separator.typeAsString})",
        )
    }
}
