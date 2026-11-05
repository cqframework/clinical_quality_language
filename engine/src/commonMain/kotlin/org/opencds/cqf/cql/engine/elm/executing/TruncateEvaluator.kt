package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.DecimalHelper
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Value

/*
Truncate(argument Decimal) Integer

The Truncate operator returns the integer component of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/
object TruncateEvaluator {
    @JvmStatic
    fun truncate(operand: Value?): Integer? {
        if (operand == null) {
            return null
        }

        if (operand is Decimal) {
            return DecimalHelper.toIntegerOrNull(operand.value.setScale(0, RoundingMode.DOWN))
        }

        throw InvalidOperatorArgument("Truncate(Decimal)", "Truncate(${operand.typeAsString})")
    }
}
