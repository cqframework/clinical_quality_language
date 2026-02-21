package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
Substring(stringToSub String, startIndex Integer) String
Substring(stringToSub String, startIndex Integer, length Integer) String

The Substring operator returns the string within stringToSub, starting at the 0-based index startIndex,
  and consisting of length characters.
If length is ommitted, the substring returned starts at startIndex and continues to the end of stringToSub.
If stringToSub or startIndex is null, or startIndex is out of range, the result is null.
*/
object SubstringEvaluator {
    @JvmStatic
    fun substring(stringValue: Any?, startIndexValue: Any?, lengthValue: Any?): Any? {
        if (stringValue == null || startIndexValue == null) {
            return null
        }

        if (stringValue is String && startIndexValue is Int) {
            val string = stringValue
            val startIndex = startIndexValue

            if (startIndex < 0 || startIndex >= string.length) {
                return null
            }

            if (lengthValue == null) {
                return string.substring(startIndex)
            } else {
                var endIndex = startIndex + lengthValue as Int
                if (endIndex > string.length) {
                    endIndex = string.length
                }

                if (endIndex < startIndex) {
                    endIndex = startIndex
                }

                return string.substring(startIndex, endIndex)
            }
        }

        throw InvalidOperatorArgument(
            "Substring(String, Integer) or Substring(String, Integer, Integer)",
            "Substring(${stringValue.javaClassName}, ${startIndexValue.javaClassName}${if (lengthValue == null) "" else ", " + lengthValue.javaClassName})",
        )
    }
}
