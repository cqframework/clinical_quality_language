package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

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
    fun splitOnMatches(stringToSplit: CqlType?, separator: CqlType?): List? {
        if (stringToSplit == null) {
            return null
        }

        if (stringToSplit is String && separator is String?) {
            val result = mutableListOf<String>()
            if (separator == null) {
                result.add(stringToSplit)
            } else {
                result.addAll(
                    stringToSplit
                        .split(separator.value.toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .map { it.toCqlString() }
                )
            }
            return result.toCqlList()
        }

        throw InvalidOperatorArgument(
            "SplitOnMatches(String, String)",
            "SplitOnMatches(${stringToSplit.typeAsString}, ${separator!!.typeAsString})",
        )
    }
}
