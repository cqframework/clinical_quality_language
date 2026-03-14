package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.QName
import org.cqframework.cql.shared.ZERO
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*

/*
maximum<T>() T

The maximum operator returns the maximum representable value for the given type.
The maximum operator is defined for the Integer, Long, Decimal, DateTime, and Time types.
For Integer, maximum returns the maximum signed 32-bit integer, 231 - 1.
For Long, maximum returns the maximum signed 64-bit Long, 263 - 1.
For Decimal, maximum returns the maximum representable decimal value, (10^28 – 1) / 10^8 (99999999999999999999.99999999).
For DateTime, maximum returns the maximum representable date/time value, DateTime(9999, 12, 31, 23, 59, 59, 999).
For Time, maximum returns the maximum representable time value, Time(23, 59, 59, 999).
For any other type, attempting to invoke maximum results in an error.
*/
@Suppress("MagicNumber")
object MaxValueEvaluator {
    @JvmStatic
    fun maxValue(type: String?): Any? {
        if (type == null) {
            return null
        }

        if (type.endsWith("Integer")) {
            return Value.MAX_INT
        }
        if (type.endsWith("Long")) {
            return Value.MAX_LONG
        }
        if (type.endsWith("Decimal")) {
            return Value.MAX_DECIMAL
        }
        if (type.endsWith("Date")) {
            return Date(9999, 12, 31)
        }
        if (type.endsWith("DateTime")) {
            return DateTime(ZERO, 9999, 12, 31, 23, 59, 59, 999)
        }
        if (type.endsWith("Time")) {
            return Time(23, 59, 59, 999)
        }
        // NOTE: Quantity max is not standard
        if (type.endsWith("Quantity")) {
            return Quantity().withValue(Value.MAX_DECIMAL).withUnit("1")
        }

        throw InvalidOperatorArgument("The Maximum operator is not implemented for type ${type}")
    }

    @JvmStatic
    fun internalEvaluate(typeName: QName?, state: State?): Any? {
        val valueType = state!!.environment.fixupQName(typeName!!)
        val type = valueType.getLocalPart()
        return maxValue(type)
    }
}
