package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Literal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Long
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong

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
    fun negate(source: CqlType?): CqlType? {
        if (source == null) {
            return null
        }

        if (source is Integer) {
            return (-source.value).toCqlInteger()
        }

        if (source is Long) {
            return (-source.value).toCqlLong()
        }

        if (source is Decimal) {
            return source.value.negate().toCqlDecimal()
        }

        if (source is Quantity) {
            val quantity = source
            return Quantity().withValue(quantity.value!!.negate()).withUnit(quantity.unit)
        }

        throw InvalidOperatorArgument(
            "Negate(Integer), Negate(Long), Negate(Decimal) or Negate(Quantity)",
            "Negate(${source.typeAsString})",
        )
    }

    fun internalEvaluate(
        operand: Expression,
        state: State?,
        visitor: ElmLibraryVisitor<CqlType?, State?>,
    ): CqlType? {
        // Special case to handle literals of the minimum Integer value
        // since usual implementation would try to cast 2147483648 as a
        // signed 32 bit signed integer and throw
        // java.lang.NumberFormatException: For input string: "2147483648".

        if (operand is Literal && operand.value.equals("2147483648")) {
            return Int.MIN_VALUE.toCqlInteger()
        }

        if (operand is Literal && operand.value.equals("9223372036854775807")) {
            return kotlin.Long.MIN_VALUE.toCqlLong()
        }

        val source = visitor.visitExpression(operand, state)

        return negate(source)
    }
}
