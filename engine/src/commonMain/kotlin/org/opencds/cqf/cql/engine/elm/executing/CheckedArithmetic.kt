package org.opencds.cqf.cql.engine.elm.executing

/**
 * Integer and Long arithmetic that returns null instead of overflowing. CQL requires arithmetic
 * whose result cannot be represented to evaluate to null rather than wrap around.
 */
internal object CheckedArithmetic {
    private fun Long.toIntOrNull(): Int? =
        if (this in Int.MIN_VALUE..Int.MAX_VALUE) this.toInt() else null

    fun add(left: Int, right: Int): Int? = (left.toLong() + right).toIntOrNull()

    fun subtract(left: Int, right: Int): Int? = (left.toLong() - right).toIntOrNull()

    fun multiply(left: Int, right: Int): Int? = (left.toLong() * right).toIntOrNull()

    fun negate(value: Int): Int? = if (value == Int.MIN_VALUE) null else -value

    fun add(left: Long, right: Long): Long? {
        val result = left + right
        // Overflow when both operands have the same sign and the result's sign differs
        return if ((left xor result) and (right xor result) < 0) null else result
    }

    fun subtract(left: Long, right: Long): Long? {
        val result = left - right
        // Overflow when the operands' signs differ and the result's sign differs from the left
        return if ((left xor right) and (left xor result) < 0) null else result
    }

    fun multiply(left: Long, right: Long): Long? {
        val result = left * right
        // Dividing back recovers the left operand unless the product wrapped. That check itself
        // wraps for minimum * -1, so that case is tested on its own.
        val minTimesMinusOne = left == Long.MIN_VALUE && right == -1L
        val wrapped = right != 0L && result / right != left
        return if (minTimesMinusOne || wrapped) null else result
    }

    fun negate(value: Long): Long? = if (value == Long.MIN_VALUE) null else -value
}
