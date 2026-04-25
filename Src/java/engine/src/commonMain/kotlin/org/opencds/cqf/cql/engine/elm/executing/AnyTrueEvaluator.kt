package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List

/*
AnyTrue(argument List<Boolean>) Boolean

The AnyTrue operator returns true if any non-null element in the source is true.
If the source contains no non-null elements, false is returned.
If the source is null, the result is null.
*/
object AnyTrueEvaluator {
    @JvmStatic
    fun anyTrue(src: CqlType?): Boolean {
        if (src == null) {
            return Boolean.FALSE
        }

        if (src is List) {
            val element = src
            val elemsItr = element.iterator()

            if (!elemsItr.hasNext()) { // empty list
                return Boolean.FALSE
            }

            while (elemsItr.hasNext()) {
                val exp = elemsItr.next()

                if (exp == null) { // skip null
                    continue
                }

                if (exp is Boolean) {
                    val boolVal = exp

                    if (boolVal.value) {
                        return Boolean.TRUE
                    }
                } else {
                    throw InvalidOperatorArgument(
                        "AnyTrue(List<Boolean>)",
                        "AnyTrue(List<${exp.typeAsString}>)",
                    )
                }
            }

            return Boolean.FALSE // all null or all false
        }

        throw InvalidOperatorArgument("AnyTrue(List<Boolean>)", "AnyTrue(${src.typeAsString})")
    }
}
