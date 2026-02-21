package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.cqframework.cql.shared.BigDecimal
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Literal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.util.javaClassName

/*
-(argument Integer) Integer
-(argument Long) Long
-(argument Decimal) Decimal
-(argument Quantity) Quantity

The negate (-) operator returns the negative of its argument.
When negating quantities, the unit is unchanged.
If the argument is null, the result is null.
*/
object NegateEvaluator {
    fun negate(source: Any?): Any? {
        if (source == null) {
            return null
        }

        if (source is Int) {
            return -source
        }

        if (source is Long) {
            return -source
        }

        if (source is BigDecimal) {
            return source.negate()
        }

        if (source is Quantity) {
            val quantity = source
            return Quantity().withValue(quantity.value!!.negate()).withUnit(quantity.unit)
        }

        throw InvalidOperatorArgument(
            "Negate(Integer), Negate(Long), Negate(Decimal) or Negate(Quantity)",
            "Negate(${source.javaClassName})",
        )
    }

    fun internalEvaluate(
        operand: Expression,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        // Special case to handle literals of the minimum Integer value
        // since usual implementation would try to cast 2147483648 as a
        // signed 32 bit signed integer and throw
        // java.lang.NumberFormatException: For input string: "2147483648".

        if (operand is Literal && operand.value.equals("2147483648")) {
            return Int.Companion.MIN_VALUE
        }

        if (operand is Literal && operand.value.equals("9223372036854775807")) {
            return Long.Companion.MIN_VALUE
        }

        val source = visitor.visitExpression(operand, state)

        return negate(source)
    }
}
