package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.util.javaClassName

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
    fun modulo(left: Any?, right: Any?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is Int) {
            if (right as Int == 0) {
                return null
            }
            return left % right
        }

        if (left is Long) {
            if (right as Long == 0L) {
                return null
            }
            return left % right
        }

        if (left is BigDecimal) {
            if (right === BigDecimal("0.0")) {
                return null
            }
            return left.remainder(right as BigDecimal).setScale(8, RoundingMode.FLOOR)
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
            "Modulo(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
