package org.cqframework.cql.shared

import com.ionspin.kotlin.bignum.decimal.BigDecimal as KtBigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode as KtRoundingMode

/** A minimal pure-Kotlin implementation of BigDecimal for non-Java environments. */
@Suppress("TooManyFunctions")
data class BigDecimalJs(private val value: KtBigDecimal) {
    constructor(value: Int) : this(KtBigDecimal.fromInt(value))

    constructor(value: Long) : this(KtBigDecimal.fromLong(value))

    constructor(value: String) : this(KtBigDecimal.parseString(value))

    constructor(value: Double) : this(KtBigDecimal.fromDouble(value))

    fun toPlainString(): String {
        return this.value.toPlainString()
    }

    fun toDouble(): Double {
        return this.value.doubleValue(false)
    }

    operator fun compareTo(value: BigDecimalJs): Int {
        return this.value.compareTo(value.value)
    }

    fun scale(): Int {
        return this.value.scale.toInt()
    }

    fun setScale(scale: Int): BigDecimalJs {
        return BigDecimalJs(this.value.scale(scale.toLong()))
    }

    fun setScale(scale: Int, roundingMode: RoundingModeJs): BigDecimalJs {
        return BigDecimalJs(
            KtBigDecimal.parseStringWithMode(
                this.value.toPlainString(),
                DecimalMode(roundingMode = roundingMode.toKtRoundingMode(), scale = scale.toLong()),
            )
        )
    }

    fun scaleByPowerOfTen(n: Int): BigDecimalJs {
        return BigDecimalJs(this.value * KtBigDecimal.fromInt(@Suppress("MagicNumber") 10).pow(n))
    }

    fun stripTrailingZeros(): BigDecimalJs {
        TODO()
    }

    fun abs(): BigDecimalJs {
        return BigDecimalJs(this.value.abs())
    }

    fun negate(): BigDecimalJs {
        return BigDecimalJs(this.value.negate())
    }

    fun add(value: BigDecimalJs): BigDecimalJs {
        return BigDecimalJs(this.value.add(value.value))
    }

    fun subtract(value: BigDecimalJs): BigDecimalJs {
        return BigDecimalJs(this.value.subtract(value.value))
    }

    fun multiply(value: BigDecimalJs): BigDecimalJs {
        return BigDecimalJs(this.value.multiply(value.value))
    }

    fun divide(value: BigDecimalJs): BigDecimalJs {
        return BigDecimalJs(this.value.divide(value.value))
    }

    fun divide(value: BigDecimalJs, scale: Int, roundingMode: RoundingModeJs): BigDecimalJs {
        return BigDecimalJs(
            this.value.divide(
                value.value,
                DecimalMode(roundingMode = roundingMode.toKtRoundingMode(), scale = scale.toLong()),
            )
        )
    }

    fun remainder(value: BigDecimalJs): BigDecimalJs {
        return BigDecimalJs(this.value.remainder(value.value))
    }

    fun divideAndRemainder(value: BigDecimalJs): Array<BigDecimalJs> {
        val result = this.value.divideAndRemainder(value.value)
        return arrayOf(BigDecimalJs(result.first), BigDecimalJs(result.second))
    }

    fun pow(value: Int): BigDecimalJs {
        return BigDecimalJs(this.value.pow(value))
    }

    fun toInt(): Int {
        return this.value.intValue()
    }

    fun toLong(): Long {
        return this.value.longValue()
    }

    override fun toString(): String {
        return this.value.toString()
    }
}

enum class RoundingModeJs {
    CEILING,
    FLOOR,
    UNNECESSARY,
    HALF_UP,
    HALF_DOWN,
    DOWN;

    fun toKtRoundingMode(): KtRoundingMode {
        return when (this) {
            CEILING -> KtRoundingMode.CEILING
            FLOOR -> KtRoundingMode.FLOOR
            UNNECESSARY -> KtRoundingMode.NONE
            HALF_UP -> KtRoundingMode.ROUND_HALF_AWAY_FROM_ZERO
            HALF_DOWN -> KtRoundingMode.ROUND_HALF_TOWARDS_ZERO
            DOWN -> KtRoundingMode.TOWARDS_ZERO
        }
    }
}
