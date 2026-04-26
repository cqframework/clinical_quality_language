package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

/*
mod(left Integer, right Integer) Integer
mod(left Long, right Long) Long
mod(left Decimal, right Decimal) Decimal
mod(left Quantity, right Quantity) Quantity

The mod operator computes the remainder of the division of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/
object ModuloEvaluator {
    @JvmStatic
    fun modulo(left: Value?, right: Value?): Value? {
        if (left == null || right == null) {
            return null
        }

        if (left is Integer && right is Integer) {
            if (right.value == 0) {
                return null
            }
            return (left.value % right.value).toCqlInteger()
        }

        if (left is Long && right is Long) {
            if (right.value == 0L) {
                return null
            }
            return (left.value % right.value).toCqlLong()
        }

        if (left is Decimal && right is Decimal) {
            if (right.value == BigDecimal("0.0")) {
                return null
            }
            return left.value.remainder(right.value).setScale(8, RoundingMode.FLOOR).toCqlDecimal()
        }

        if (left is Quantity) {
            if ((right as Quantity).value!!.compareTo(BigDecimal("0.0")) == 0) {
                return null
            }

            return Quantity()
                .withUnit(left.unit)
                .withValue(left.value!!.remainder(right.value!!).setScale(8, RoundingMode.FLOOR))
        }

        throw InvalidOperatorArgument(
            "Modulo(Integer, Integer), Modulo(Long, Long) or Modulo(Decimal, Decimal), , Modulo(Quantity, Quantity)",
            "Modulo(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
