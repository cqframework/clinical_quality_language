package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
AnyTrue(argument List<Boolean>) Boolean

The AnyTrue operator returns true if any non-null element in the source is true.
If the source contains no non-null elements, false is returned.
If the source is null, the result is null.
*/
object AnyTrueEvaluator {
    @JvmStatic
    fun anyTrue(src: Any?): Boolean? {
        if (src == null) {
            return false
        }

        if (src is Iterable<*>) {
            val element = src
            val elemsItr = element.iterator()

            if (!elemsItr.hasNext()) { // empty list
                return false
            }

            while (elemsItr.hasNext()) {
                val exp = elemsItr.next()

                if (exp == null) { // skip null
                    continue
                }

                if (exp is Boolean) {
                    val boolVal = exp

                    if (boolVal) {
                        return true
                    }
                } else {
                    throw InvalidOperatorArgument(
                        "AnyTrue(List<Boolean>)",
                        "AnyTrue(List<${exp.javaClassName}>)",
                    )
                }
            }

            return false // all null or all false
        }

        throw InvalidOperatorArgument("AnyTrue(List<Boolean>)", "AnyTrue(${src.javaClassName})")
    }
}
