package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.QName
import org.cqframework.cql.shared.ZERO
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*

/*
minimum<T>() T

The minimum operator returns the minimum representable value for the given type.
The minimum operator is defined for the Integer, Long, Decimal, DateTime, and Time types.
For Integer, minimum returns the minimum signed 32-bit integer, -231.
For Long, minimum returns the minimum signed 32-bit integer, -263.
For Decimal, minimum returns the minimum representable decimal value, (-10^28 + 1) / 10^8 (-99999999999999999999.99999999).
For DateTime, minimum returns the minimum representable date/time value, DateTime(1, 1, 1, 0, 0, 0, 0).
For Time, minimum returns the minimum representable time value, Time(0, 0, 0, 0).
For any other type, attempting to invoke minimum results in an error.
*/
object MinValueEvaluator {
    @JvmStatic
    fun minValue(type: String?): Any? {
        if (type == null) {
            return null
        }

        if (type.endsWith("Integer")) {
            return Value.MIN_INT
        }
        if (type.endsWith("Long")) {
            return Value.MIN_LONG
        }
        if (type.endsWith("Decimal")) {
            return Value.MIN_DECIMAL
        }
        if (type.endsWith("Date")) {
            return Date(1, 1, 1)
        }
        if (type.endsWith("DateTime")) {
            return DateTime(ZERO, 1, 1, 1, 0, 0, 0, 0)
        }
        if (type.endsWith("Time")) {
            return Time(0, 0, 0, 0)
        }
        // NOTE: Quantity min is not standard
        if (type.endsWith("Quantity")) {
            return Quantity().withValue(Value.MIN_DECIMAL).withUnit("1")
        }

        throw InvalidOperatorArgument("The Minimum operator is not implemented for type ${type}")
    }

    @JvmStatic
    fun internalEvaluate(vtype: QName?, state: State?): Any? {
        val valueType = state!!.environment.fixupQName(vtype!!)
        val type = valueType.getLocalPart()
        return minValue(type)
    }
}
