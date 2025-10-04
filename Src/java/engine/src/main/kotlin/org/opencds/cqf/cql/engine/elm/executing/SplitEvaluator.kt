package org.opencds.cqf.cql.engine.elm.executing

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
    fun split(stringToSplit: Any?, separator: Any?): Any? {
        if (stringToSplit == null) {
            return null
        }

        if (stringToSplit is String) {
            val result: MutableList<Any?> = ArrayList<Any?>()
            if (separator == null) {
                result.add(stringToSplit)
            } else {
                result.addAll(stringToSplit.split(separator as String))
            }
            return result
        }

        throw InvalidOperatorArgument(
            "Split(String, String)",
            String.format("Split(%s, %s)", stringToSplit.javaClass.name, separator!!.javaClass.name),
        )
    }
}
