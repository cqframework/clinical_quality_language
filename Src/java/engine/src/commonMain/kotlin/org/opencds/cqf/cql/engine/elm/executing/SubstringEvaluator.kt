package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlString

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
    fun substring(
        stringValue: CqlType?,
        startIndexValue: CqlType?,
        lengthValue: CqlType?,
    ): String? {
        if (stringValue == null || startIndexValue == null) {
            return null
        }

        if (stringValue is String && startIndexValue is Integer && lengthValue is Integer?) {
            val string = stringValue
            val startIndex = startIndexValue.value

            if (startIndex < 0 || startIndex >= string.length) {
                return null
            }

            if (lengthValue == null) {
                return string.substring(startIndex).toCqlString()
            } else {
                var endIndex = startIndex + lengthValue.value
                if (endIndex > string.length) {
                    endIndex = string.length
                }

                if (endIndex < startIndex) {
                    endIndex = startIndex
                }

                return string.substring(startIndex, endIndex).toCqlString()
            }
        }

        throw InvalidOperatorArgument(
            "Substring(String, Integer) or Substring(String, Integer, Integer)",
            "Substring(${stringValue.typeAsString}, ${startIndexValue.typeAsString}${if (lengthValue == null) "" else ", " + lengthValue.typeAsString})",
        )
    }
}
