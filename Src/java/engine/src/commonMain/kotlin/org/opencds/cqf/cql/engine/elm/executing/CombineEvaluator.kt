package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Combine(source List<String>) String
Combine(source List<String>, separator String) String

The Combine operator combines a list of strings, optionally separating each string with the given separator.
If either argument is null, or any element in the source list of strings is null, the result is null.
*/
object CombineEvaluator {
    @JvmStatic
    fun combine(source: Any?, separator: String?): Any? {
        if (source == null || separator == null) {
            return null
        } else {
            if (source is Iterable<*>) {
                val buffer = StringBuilder("")
                val iterator = source.iterator()
                var first = true

                while (iterator.hasNext()) {
                    val item = iterator.next()

                    if (item == null) {
                        return null
                    }

                    if (item is String) {
                        if (!first) {
                            buffer.append(separator)
                        } else {
                            first = false
                        }
                        buffer.append(item)
                    } else {
                        throw InvalidOperatorArgument(
                            "Combine(List<String>) or Combine(List<String>, String)",
                            "Combine(List<${item.javaClassName}>${if (separator == "") "" else ", " + separator})",
                        )
                    }
                }
                return buffer.toString()
            }
        }

        throw InvalidOperatorArgument(
            "Combine(List<String>) or Combine(List<String>, String)",
            "Combine(${source.javaClassName}${if (separator == "") "" else ", " + separator})",
        )
    }
}
