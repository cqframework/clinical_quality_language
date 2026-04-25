package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString
import org.opencds.cqf.cql.engine.util.stringUtilsSplit

/*
Split(stringToSplit String, separator String) List<String>

The Split operator splits a string into a list of strings using a separator.
If the stringToSplit argument is null, the result is null.
If the stringToSplit argument does not contain any appearances of the separator,
  the result is a list of strings containing one element that is the value of the stringToSplit argument.
*/
object SplitEvaluator {
    @JvmStatic
    @Suppress("ReturnCount")
    fun split(stringToSplit: CqlType?, separator: CqlType?): List? {
        if (stringToSplit == null) {
            return null
        }

        if (stringToSplit is String && separator is String?) {
            if (separator == null) {
                return mutableListOf(stringToSplit).toCqlList()
            }
            return stringUtilsSplit(stringToSplit.value, separator.value)
                .map { it.toCqlString() }
                .toCqlList()
        }

        throw InvalidOperatorArgument(
            "Split(String, String)",
            "Split(${stringToSplit.typeAsString}, ${separator!!.typeAsString})",
        )
    }
}
