package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List

/*
AllTrue(argument List<Boolean>) Boolean

The AllTrue operator returns true if all the non-null elements in the source are true.
If the source contains no non-null elements, true is returned.
If the source is null, the result is null.
*/
object AllTrueEvaluator {
    @JvmStatic
    fun allTrue(src: CqlType?): Boolean {
        if (src == null) {
            return Boolean.TRUE
        }

        if (src is List) {
            val element = src
            val elemsItr = element.iterator()

            if (!elemsItr.hasNext()) { // empty list
                return Boolean.TRUE
            }

            while (elemsItr.hasNext()) {
                val exp = elemsItr.next()

                if (exp == null) { // skip null
                    continue
                }

                if (exp is Boolean) {
                    val boolVal = exp

                    if (!boolVal.value) {
                        return Boolean.FALSE
                    }
                } else {
                    throw InvalidOperatorArgument(
                        "AllTrue(List<Boolean>)",
                        "AllTrue(List<${exp.typeAsString}>)",
                    )
                }
            }
            return Boolean.TRUE
        }

        throw InvalidOperatorArgument("AllTrue(List<Boolean>)", "AllTrue(${src.typeAsString})")
    }
}
