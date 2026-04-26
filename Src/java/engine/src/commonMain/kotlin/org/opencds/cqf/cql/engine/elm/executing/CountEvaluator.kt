package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/*
Count(argument List<T>) Integer

* The Count operator returns the number of non-null elements in the source.
* If the list contains no non-null elements, the result is 0.
* If the list is null, the result is null.
* Always returns Integer
*/
object CountEvaluator {
    @JvmStatic
    fun count(source: Value?): Integer {
        if (source == null) {
            return Integer(0)
        }

        var size = 0

        if (source is List) {
            val element = source
            val itr = element.iterator()

            if (!itr.hasNext()) { // empty list
                return size.toCqlInteger()
            }

            while (itr.hasNext()) {
                val value = itr.next()

                if (value == null) { // skip null
                    continue
                }

                ++size
            }

            return size.toCqlInteger()
        }

        throw InvalidOperatorArgument("Count(List<T>)", "Count(${source.typeAsString})")
    }
}
