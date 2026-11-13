package org.cqframework.cql.shared

import com.ionspin.kotlin.bignum.decimal.RoundingMode as KtRoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * A pure-Kotlin implementation of BigDecimal for non-Java environments, with the semantics of
 * java.math.BigDecimal: an unscaled integer and a scale, so that results keep their exact value and
 * scale as they do on the JVM.
 */
@Suppress("TooManyFunctions")
@JsOnlyExport
@JsName("BigDecimal")
class BigDecimalJs private constructor(private val unscaled: BigInteger, private val scale: Int) {
    @JsName("fromInt") constructor(value: Int) : this(BigInteger.fromInt(value), 0)

    @JsName("fromLong") constructor(value: Long) : this(BigInteger.fromLong(value), 0)

    @JsName("fromString") constructor(value: String) : this(parse(value))

    @JsName("fromDouble") constructor(value: Double) : this(fromDoubleExact(value))

    private constructor(parts: Pair<BigInteger, Int>) : this(parts.first, parts.second)

    fun toPlainString(): String {
        val digits = unscaled.abs().toString()
        val sign = if (unscaled.signum() < 0) "-" else ""
        return when {
            scale == 0 -> sign + digits
            scale < 0 -> if (unscaled.isZero()) "0" else sign + digits + "0".repeat(-scale)
            digits.length > scale ->
                sign +
                    digits.substring(0, digits.length - scale) +
                    "." +
                    digits.substring(digits.length - scale)
            else -> sign + "0." + "0".repeat(scale - digits.length) + digits
        }
    }

    fun toDouble(): Double {
        return this.toString().toDouble()
    }

    operator fun compareTo(value: BigDecimalJs): Int {
        val common = maxOf(this.scale, value.scale)
        return this.unscaledAt(common).compareTo(value.unscaledAt(common))
    }

    fun scale(): Int {
        return this.scale
    }

    @JsExport.Ignore
    fun setScale(scale: Int): BigDecimalJs {
        return setScale(scale, RoundingModeJs.UNNECESSARY)
    }

    @JsExport.Ignore
    fun setScale(scale: Int, roundingMode: RoundingModeJs): BigDecimalJs {
        return if (scale >= this.scale) {
            BigDecimalJs(this.unscaled * tenToThe(scale - this.scale), scale)
        } else {
            BigDecimalJs(
                divideRounded(this.unscaled, tenToThe(this.scale - scale), roundingMode),
                scale,
            )
        }
    }

    fun scaleByPowerOfTen(n: Int): BigDecimalJs {
        return BigDecimalJs(this.unscaled, this.scale - n)
    }

    fun stripTrailingZeros(): BigDecimalJs {
        if (this.unscaled.isZero()) {
            return BigDecimalJs(BigInteger.ZERO, 0)
        }
        var digits = this.unscaled
        var newScale = this.scale
        while (true) {
            val (quotient, remainder) = digits.divrem(BigInteger.TEN)
            if (!remainder.isZero()) break
            digits = quotient
            newScale--
        }
        return BigDecimalJs(digits, newScale)
    }

    fun abs(): BigDecimalJs {
        return if (this.unscaled.signum() < 0) negate() else this
    }

    fun negate(): BigDecimalJs {
        return BigDecimalJs(this.unscaled.negate(), this.scale)
    }

    fun add(value: BigDecimalJs): BigDecimalJs {
        val common = maxOf(this.scale, value.scale)
        return BigDecimalJs(this.unscaledAt(common) + value.unscaledAt(common), common)
    }

    fun subtract(value: BigDecimalJs): BigDecimalJs {
        val common = maxOf(this.scale, value.scale)
        return BigDecimalJs(this.unscaledAt(common) - value.unscaledAt(common), common)
    }

    fun multiply(value: BigDecimalJs): BigDecimalJs {
        return BigDecimalJs(this.unscaled * value.unscaled, this.scale + value.scale)
    }

    /**
     * Divides exactly, with the scale java.math.BigDecimal.divide gives: the preferred scale
     * (this.scale - value.scale) if the quotient can be written with it, else the smallest larger
     * scale that writes it exactly. Throws ArithmeticException when the quotient has no finite
     * decimal expansion.
     */
    @JsExport.Ignore
    fun divide(value: BigDecimalJs): BigDecimalJs {
        checkDivisor(value)
        val preferredScale = this.scale - value.scale
        if (this.unscaled.isZero()) {
            return BigDecimalJs(BigInteger.ZERO, preferredScale)
        }
        val gcd = gcd(this.unscaled.abs(), value.unscaled.abs())
        var numerator = this.unscaled / gcd
        var denominator = value.unscaled / gcd
        if (denominator.signum() < 0) {
            numerator = numerator.negate()
            denominator = denominator.negate()
        }
        var twos = 0
        var fives = 0
        var rest = denominator
        while (rest.rem(TWO).isZero()) {
            rest /= TWO
            twos++
        }
        while (rest.rem(FIVE).isZero()) {
            rest /= FIVE
            fives++
        }
        if (rest != BigInteger.ONE) {
            throw ArithmeticException(
                "Non-terminating decimal expansion; no exact representable decimal result."
            )
        }
        val places = maxOf(twos, fives)
        return BigDecimalJs(numerator * (tenToThe(places) / denominator), preferredScale + places)
    }

    @JsExport.Ignore
    fun divide(value: BigDecimalJs, scale: Int, roundingMode: RoundingModeJs): BigDecimalJs {
        checkDivisor(value)
        // this / value = (u1 / u2) * 10^(s2 - s1); at the requested scale the unscaled result is
        // u1 * 10^(scale + s2 - s1) / u2.
        val exponent = scale + value.scale - this.scale
        val result =
            if (exponent >= 0) {
                divideRounded(this.unscaled * tenToThe(exponent), value.unscaled, roundingMode)
            } else {
                divideRounded(this.unscaled, value.unscaled * tenToThe(-exponent), roundingMode)
            }
        return BigDecimalJs(result, scale)
    }

    fun remainder(value: BigDecimalJs): BigDecimalJs {
        return this.subtract(this.divideToIntegralValue(value).multiply(value))
    }

    fun divideAndRemainder(value: BigDecimalJs): Array<BigDecimalJs> {
        val quotient = this.divideToIntegralValue(value)
        return arrayOf(quotient, this.subtract(quotient.multiply(value)))
    }

    fun pow(value: Int): BigDecimalJs {
        if (value < 0) {
            throw ArithmeticException("Invalid operation")
        }
        if (value == 0) {
            // kotlin-bignum gives 0 for 0^0; java.math.BigDecimal gives 1.
            return BigDecimalJs(BigInteger.ONE, 0)
        }
        return BigDecimalJs(this.unscaled.pow(value), this.scale * value)
    }

    fun toInt(): Int {
        return this.toLong().toInt()
    }

    /** The integer part, keeping its low-order 64 bits, as java.math.BigDecimal.longValue does. */
    fun toLong(): Long {
        val integerPart = this.setScale(0, RoundingModeJs.DOWN).unscaled
        var low = integerPart.rem(TWO_TO_THE_64)
        if (low.signum() < 0) low += TWO_TO_THE_64
        if (low >= TWO_TO_THE_63) low -= TWO_TO_THE_64
        return low.toString().toLong()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is BigDecimalJs && this.scale == other.scale && this.unscaled == other.unscaled
    }

    override fun hashCode(): Int {
        return 31 * this.unscaled.hashCode() + this.scale
    }

    /** The string java.math.BigDecimal.toString gives, in scientific notation when it uses it. */
    override fun toString(): String {
        val digits = this.unscaled.abs().toString()
        val adjusted = -this.scale.toLong() + (digits.length - 1)
        if (this.scale >= 0 && adjusted >= PLAIN_NOTATION_MIN_EXPONENT) {
            return toPlainString()
        }
        val sign = if (this.unscaled.signum() < 0) "-" else ""
        val mantissa =
            if (digits.length > 1) digits.substring(0, 1) + "." + digits.substring(1) else digits
        val exponent =
            when {
                adjusted == 0L -> ""
                adjusted > 0 -> "E+$adjusted"
                else -> "E$adjusted"
            }
        return sign + mantissa + exponent
    }

    /** The integer part of the quotient, with the scale java.math.BigDecimal gives it. */
    private fun divideToIntegralValue(value: BigDecimalJs): BigDecimalJs {
        checkDivisor(value)
        val preferredScale = this.scale - value.scale
        // Truncated integer quotient of (u1 * 10^-s1) / (u2 * 10^-s2).
        val common = maxOf(this.scale, value.scale)
        val integer = this.unscaledAt(common) / value.unscaledAt(common)
        return when {
            preferredScale >= 0 -> BigDecimalJs(integer * tenToThe(preferredScale), preferredScale)
            integer.isZero() -> BigDecimalJs(BigInteger.ZERO, preferredScale)
            else -> withTrailingZerosRemoved(integer, preferredScale)
        }
    }

    /**
     * integer with its trailing zeros moved into a negative scale, down to at most minScale, as
     * java.math.BigDecimal keeps a negative preferred scale.
     */
    private fun withTrailingZerosRemoved(integer: BigInteger, minScale: Int): BigDecimalJs {
        var digits = integer
        var newScale = 0
        while (newScale > minScale) {
            val (quotient, remainder) = digits.divrem(BigInteger.TEN)
            if (!remainder.isZero()) break
            digits = quotient
            newScale--
        }
        return BigDecimalJs(digits, newScale)
    }

    private fun unscaledAt(targetScale: Int): BigInteger {
        return this.unscaled * tenToThe(targetScale - this.scale)
    }

    private fun checkDivisor(value: BigDecimalJs) {
        if (value.unscaled.isZero()) {
            throw ArithmeticException(
                if (this.unscaled.isZero()) "Division undefined" else "Division by zero"
            )
        }
    }
}

@JsOnlyExport
@JsName("RoundingMode")
enum class RoundingModeJs {
    CEILING,
    FLOOR,
    UNNECESSARY,
    HALF_UP,
    HALF_DOWN,
    DOWN;

    @Suppress("NON_EXPORTABLE_TYPE")
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

/** The smallest adjusted exponent java.math.BigDecimal.toString writes without an exponent. */
private const val PLAIN_NOTATION_MIN_EXPONENT = -6
private const val DECIMAL_RADIX = 10
private const val LONG_BITS = 64
private const val DOUBLE_SIGNIFICAND_BITS = 52
private const val DOUBLE_EXPONENT_MASK = 0x7ffL
private const val DOUBLE_SIGNIFICAND_MASK = 0x000fffffffffffffL
private const val DOUBLE_EXPONENT_BIAS = 1075
private const val DOUBLE_SUBNORMAL_EXPONENT = -1074

private val TWO = BigInteger.TWO
private val FIVE = BigInteger.TEN / BigInteger.TWO
private val TWO_TO_THE_63 = BigInteger.TWO.pow(LONG_BITS - 1)
private val TWO_TO_THE_64 = BigInteger.TWO.pow(LONG_BITS)

private fun tenToThe(n: Int): BigInteger = BigInteger.TEN.pow(n)

private fun gcd(a: BigInteger, b: BigInteger): BigInteger {
    var x = a
    var y = b
    while (!y.isZero()) {
        val t = x.rem(y)
        x = y
        y = t
    }
    return x
}

/** numerator / denominator, rounded to an integer with the given mode. */
private fun divideRounded(
    numerator: BigInteger,
    denominator: BigInteger,
    roundingMode: RoundingModeJs,
): BigInteger {
    val (quotient, remainder) = numerator.divrem(denominator)
    if (remainder.isZero()) {
        return quotient
    }
    val sign = numerator.signum() * denominator.signum()
    val awayFromZero = quotient + BigInteger.fromInt(sign)
    val half = (remainder.abs() * TWO).compareTo(denominator.abs())
    return when (roundingMode) {
        RoundingModeJs.DOWN -> quotient
        RoundingModeJs.CEILING -> if (sign > 0) awayFromZero else quotient
        RoundingModeJs.FLOOR -> if (sign < 0) awayFromZero else quotient
        RoundingModeJs.HALF_UP -> if (half >= 0) awayFromZero else quotient
        RoundingModeJs.HALF_DOWN -> if (half > 0) awayFromZero else quotient
        RoundingModeJs.UNNECESSARY -> throw ArithmeticException("Rounding necessary")
    }
}

/** The unscaled value and scale of a decimal string, by java.math.BigDecimal(String)'s grammar. */
private fun parse(value: String): Pair<BigInteger, Int> {
    val groups = DECIMAL.matchEntire(value)?.groups
    val whole = groups?.get("whole")?.value.orEmpty()
    val fraction = groups?.get("fraction")?.value.orEmpty()
    val exponent = (groups?.get("exponent")?.value ?: "0").toLongOrNull()
    val hasDigits = whole.isNotEmpty() || fraction.isNotEmpty()
    if (groups == null || exponent == null || !hasDigits) {
        throw NumberFormatException("Invalid decimal: $value")
    }
    val digits = (whole + fraction).trimStart('0').ifEmpty { "0" }
    val magnitude = BigInteger.parseString(digits, DECIMAL_RADIX)
    val scale = fraction.length.toLong() - exponent
    if (scale !in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
        throw NumberFormatException("Scale out of range: $value")
    }
    return (if (groups["sign"]?.value == "-") magnitude.negate() else magnitude) to scale.toInt()
}

private val DECIMAL =
    Regex("""(?<sign>[+-]?)(?<whole>\d*)(?:\.(?<fraction>\d*))?(?:[eE](?<exponent>[+-]?\d+))?""")

/**
 * The exact binary value of a double, as java.math.BigDecimal(double) gives it: significand ×
 * 2^exponent, written exactly in decimal.
 */
private fun fromDoubleExact(value: Double): Pair<BigInteger, Int> {
    if (value.isNaN() || value.isInfinite()) {
        throw NumberFormatException("Infinite or NaN")
    }
    val bits = value.toRawBits()
    val negative = bits < 0
    val exponentBits = ((bits shr DOUBLE_SIGNIFICAND_BITS) and DOUBLE_EXPONENT_MASK).toInt()
    var significand = bits and DOUBLE_SIGNIFICAND_MASK
    var exponent: Int
    if (exponentBits == 0) {
        exponent = DOUBLE_SUBNORMAL_EXPONENT
    } else {
        significand = significand or (1L shl DOUBLE_SIGNIFICAND_BITS)
        exponent = exponentBits - DOUBLE_EXPONENT_BIAS
    }
    if (significand == 0L) {
        return BigInteger.ZERO to 0
    }
    while (significand and 1L == 0L) {
        significand = significand shr 1
        exponent++
    }
    var unscaled = BigInteger.fromLong(significand)
    val scale: Int
    if (exponent < 0) {
        unscaled *= FIVE.pow(-exponent)
        scale = -exponent
    } else {
        unscaled *= TWO.pow(exponent)
        scale = 0
    }
    return (if (negative) unscaled.negate() else unscaled) to scale
}
