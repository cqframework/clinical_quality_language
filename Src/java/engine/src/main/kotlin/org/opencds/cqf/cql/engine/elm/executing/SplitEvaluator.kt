package org.opencds.cqf.cql.engine.elm.executing

import org.apache.commons.lang3.StringUtils
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument

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
    fun split(stringToSplit: Any?, separator: Any?): Any? {
        if (stringToSplit == null) {
            return null
        }

        if (stringToSplit is String) {
            if (separator == null) {
                return mutableListOf(stringToSplit)
            }
            return StringUtils.split(stringToSplit, separator as String).toMutableList()
        }

        throw InvalidOperatorArgument(
            "Split(String, String)",
            "Split(${stringToSplit.javaClass.name}, ${separator!!.javaClass.name})",
        )
    }
}
