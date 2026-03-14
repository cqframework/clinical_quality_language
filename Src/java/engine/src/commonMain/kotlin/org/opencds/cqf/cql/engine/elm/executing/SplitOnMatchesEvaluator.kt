package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    SplitOnMatches(stringToSplit String, separatorPattern String) List<String>

    The SplitOnMatches operator splits a string into a list of strings using a separator that is defined by a regular
        expression pattern.

    If the stringToSplit argument is null, the result is null.

    The separatorPattern argument is interpreted with the same regular expression semantics as defined for the Matches operator.

    If the stringToSplit argument does not contain any matches for the separatorPattern, the result is a list of strings
        containing one element that is the value of the stringToSplit argument.

*/
object SplitOnMatchesEvaluator {
    @JvmStatic
    fun splitOnMatches(stringToSplit: Any?, separator: Any?): Any? {
        if (stringToSplit == null) {
            return null
        }

        if (stringToSplit is String) {
            val result: MutableList<Any?> = ArrayList<Any?>()
            if (separator == null) {
                result.add(stringToSplit)
            } else {
                result.addAll(
                    stringToSplit.split((separator as String).toRegex()).dropLastWhile {
                        it.isEmpty()
                    }
                )
            }
            return result
        }

        throw InvalidOperatorArgument(
            "SplitOnMatches(String, String)",
            "SplitOnMatches(${stringToSplit.javaClassName}, ${separator!!.javaClassName})",
        )
    }
}
