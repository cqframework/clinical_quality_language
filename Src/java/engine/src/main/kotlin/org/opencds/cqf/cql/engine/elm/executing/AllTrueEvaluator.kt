package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

/*
AllTrue(argument List<Boolean>) Boolean

The AllTrue operator returns true if all the non-null elements in the source are true.
If the source contains no non-null elements, true is returned.
If the source is null, the result is null.
*/
object AllTrueEvaluator {
    @JvmStatic
    fun allTrue(src: Any?): Boolean? {
        if (src == null) {
            return true
        }

        if (src is Iterable<*>) {
            val element = src
            val elemsItr = element.iterator()

            if (!elemsItr.hasNext()) { // empty list
                return true
            }

            while (elemsItr.hasNext()) {
                val exp = elemsItr.next()

                if (exp == null) { // skip null
                    continue
                }

                if (exp is Boolean) {
                    val boolVal = exp

                    if (!boolVal) {
                        return false
                    }
                } else {
                    throw InvalidOperatorArgument(
                        "AllTrue(List<Boolean>)",
                        String.format("AllTrue(List<%s>)", exp.javaClass.name),
                    )
                }
            }
            return true
        }

        throw InvalidOperatorArgument(
            "AllTrue(List<Boolean>)",
            String.format("AllTrue(%s)", src.javaClass.name),
        )
    }
}
