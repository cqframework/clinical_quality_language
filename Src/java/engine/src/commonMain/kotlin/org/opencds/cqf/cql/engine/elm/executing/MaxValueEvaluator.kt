package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.QName
import org.cqframework.cql.shared.ZERO
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.dateTimeTypeName
import org.opencds.cqf.cql.engine.runtime.dateTypeName
import org.opencds.cqf.cql.engine.runtime.decimalTypeName
import org.opencds.cqf.cql.engine.runtime.integerTypeName
import org.opencds.cqf.cql.engine.runtime.longTypeName
import org.opencds.cqf.cql.engine.runtime.quantityTypeName
import org.opencds.cqf.cql.engine.runtime.timeTypeName
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

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
    fun maxValue(type: QName?): CqlType? {
        if (type == null) {
            return null
        }

        return when (type) {
            integerTypeName -> Value.MAX_INT.toCqlInteger()
            longTypeName -> Value.MAX_LONG.toCqlLong()
            decimalTypeName -> Value.MAX_DECIMAL.toCqlDecimal()
            dateTypeName -> Date(9999, 12, 31)
            dateTimeTypeName -> DateTime(ZERO, 9999, 12, 31, 23, 59, 59, 999)
            timeTypeName -> Time(23, 59, 59, 999)
            // NOTE: Quantity max is not standard
            quantityTypeName -> {
                Quantity().withValue(Value.MAX_DECIMAL).withUnit("1")
            }
            else ->
                throw InvalidOperatorArgument(
                    "The Maximum operator is not implemented for type $type"
                )
        }
    }

    @JvmStatic
    fun internalEvaluate(typeName: QName?, state: State?): CqlType? {
        val valueType = state!!.environment.fixupQName(typeName!!)
        return maxValue(valueType)
    }
}
