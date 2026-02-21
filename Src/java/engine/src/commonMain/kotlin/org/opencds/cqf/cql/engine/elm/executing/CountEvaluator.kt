package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Count(argument List<T>) Integer

* The Count operator returns the number of non-null elements in the source.
* If the list contains no non-null elements, the result is 0.
* If the list is null, the result is null.
* Always returns Integer
*/
object CountEvaluator {
    @JvmStatic
    fun count(source: Any?): Any? {
        if (source == null) {
            return 0
        }

        var size = 0

        if (source is Iterable<*>) {
            val element = source
            val itr = element.iterator()

            if (!itr.hasNext()) { // empty list
                return size
            }

            while (itr.hasNext()) {
                val value = itr.next()

                if (value == null) { // skip null
                    continue
                }

                ++size
            }

            return size
        }

        throw InvalidOperatorArgument("Count(List<T>)", "Count(${source.javaClassName})")
    }
}
