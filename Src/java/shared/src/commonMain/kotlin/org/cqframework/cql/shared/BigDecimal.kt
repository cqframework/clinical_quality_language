package org.cqframework.cql.shared

/** A minimal multiplatform implementation of BigDecimal. */
@Suppress("TooManyFunctions")
expect class BigDecimal {
    constructor(@Suppress("UnusedPrivateProperty") value: Int)

    constructor(@Suppress("UnusedPrivateProperty") value: Long)

    constructor(@Suppress("UnusedPrivateProperty") value: String)

    constructor(@Suppress("UnusedPrivateProperty") value: Double)

    fun toPlainString(): String

    fun toDouble(): Double

    operator fun compareTo(value: BigDecimal): Int

    fun scale(): Int

    fun setScale(scale: Int): BigDecimal

    fun setScale(scale: Int, roundingMode: RoundingMode): BigDecimal

    fun scaleByPowerOfTen(n: Int): BigDecimal

    fun stripTrailingZeros(): BigDecimal

    fun abs(): BigDecimal

    fun negate(): BigDecimal

    fun add(value: BigDecimal): BigDecimal

    fun subtract(value: BigDecimal): BigDecimal

    fun multiply(value: BigDecimal): BigDecimal

    fun divide(value: BigDecimal): BigDecimal

    fun divide(value: BigDecimal, scale: Int, roundingMode: RoundingMode): BigDecimal

    fun remainder(value: BigDecimal): BigDecimal

    fun divideAndRemainder(value: BigDecimal): Array<BigDecimal>

    fun pow(value: Int): BigDecimal

    fun toInt(): Int

    fun toLong(): Long
}

expect enum class RoundingMode {
    CEILING,
    FLOOR,
    UNNECESSARY,
    HALF_UP,
    HALF_DOWN,
    DOWN,
}

val ZERO = BigDecimal("0")
val ONE = BigDecimal("1")
