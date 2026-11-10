package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Constants.MAX_INT
import org.opencds.cqf.cql.engine.runtime.Constants.MIN_INT

/**
 * Integer and Long arithmetic that returns null when the result cannot be represented, as the CQL
 * specification requires for arithmetic overflow and underflow
 * (https://cql.hl7.org/09-b-cqlreference.html#arithmetic-operators-4).
 *
 * Kotlin's Int and Long arithmetic wraps around silently, and Math.addExact and similar are not
 * available in common code, so the checks are done here.
 */
object OverflowHelper {
    /** Returns the value as an Integer, or null if it is outside the range of Integer values. */
    fun toIntegerOrNull(value: kotlin.Long): Integer? {
        if (value > MAX_INT || value < MIN_INT) {
            return null
        }
        return value.toInt().toCqlInteger()
    }

    fun add(left: Int, right: Int): Integer? = toIntegerOrNull(left.toLong() + right.toLong())

    fun subtract(left: Int, right: Int): Integer? = toIntegerOrNull(left.toLong() - right.toLong())

    fun multiply(left: Int, right: Int): Integer? = toIntegerOrNull(left.toLong() * right.toLong())

    fun add(left: kotlin.Long, right: kotlin.Long): Long? {
        val result = left + right
        // Overflow happened if both operands have the same sign and the result has the other sign.
        if (((left xor result) and (right xor result)) < 0) {
            return null
        }
        return result.toCqlLong()
    }

    fun subtract(left: kotlin.Long, right: kotlin.Long): Long? {
        val result = left - right
        // Overflow happened if the operands have different signs and the result's sign differs
        // from the left operand's.
        if (((left xor right) and (left xor result)) < 0) {
            return null
        }
        return result.toCqlLong()
    }

    fun multiply(left: kotlin.Long, right: kotlin.Long): Long? =
        DecimalHelper.toLongOrNull(BigDecimal(left).multiply(BigDecimal(right)))
}
