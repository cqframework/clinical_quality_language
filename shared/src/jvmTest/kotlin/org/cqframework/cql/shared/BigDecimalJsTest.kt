package org.cqframework.cql.shared

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Compares BigDecimalJs, the implementation used on JS and Wasm, with java.math.BigDecimal on the
 * JVM over generated values: each operation must give the same string and scale, or throw the same
 * type of exception.
 */
class BigDecimalJsTest {
    private val random = Random(20261027)

    private fun decimalString(): String {
        val sign = listOf("", "-", "+")[random.nextInt(3)]
        val whole = digits(random.nextInt(0, 14))
        val fraction = if (random.nextBoolean()) "." + digits(random.nextInt(0, 10)) else ""
        val exponent = if (random.nextInt(4) == 0) "E" + random.nextInt(-12, 13) else ""
        val text = sign + whole + fraction + exponent
        return if (whole.isEmpty() && fraction.length <= 1) sign + "0" + exponent else text
    }

    private fun digits(n: Int): String {
        val small = random.nextInt(3) == 0
        return (1..n).joinToString("") {
            (if (small) random.nextInt(0, 3) else random.nextInt(0, 10)).toString()
        }
    }

    private val fixed =
        listOf(
            "0",
            "0.0",
            "0.00",
            "1",
            "-1",
            "1.0",
            "1.00",
            "0.02",
            "0.03",
            "1.5",
            "3.0",
            "10.0",
            "10.1",
            "-3.1",
            "8.0",
            "1E+3",
            "1E-3",
            "0E+3",
            "123.456",
            "-0.0001",
            "99999999999999999999.9",
            "2147483648",
            "-9223372036854775809",
        )

    private fun values(n: Int): List<String> = fixed + List(n) { decimalString() }

    @Suppress("SwallowedException")
    private fun outcome(block: () -> Any?): String =
        try {
            when (val r = block()) {
                is java.math.BigDecimal -> "${r} scale ${r.scale()}"
                is BigDecimalJs -> "${r} scale ${r.scale()}"
                is Array<*> -> r.joinToString(" | ") { outcome { it } }
                else -> r.toString()
            }
        } catch (e: ArithmeticException) {
            "ArithmeticException"
        } catch (e: NumberFormatException) {
            "NumberFormatException"
        }

    private val modes = RoundingModeJs.entries

    private fun java(mode: RoundingModeJs) = java.math.RoundingMode.valueOf(mode.name)

    @Test
    fun unaryOperationsAgreeWithJava() {
        val mismatches = mutableListOf<String>()
        fun check(what: String, js: () -> Any?, jvm: () -> Any?) {
            val a = outcome(js)
            val b = outcome(jvm)
            if (a != b) mismatches += "$what: BigDecimalJs $a, java.math.BigDecimal $b"
        }
        for (s in values(3000)) {
            val js = BigDecimalJs(s)
            val jvm = java.math.BigDecimal(s)
            check("new($s)", { js }, { jvm })
            check("$s.toPlainString", { js.toPlainString() }, { jvm.toPlainString() })
            check("$s.toDouble", { js.toDouble() }, { jvm.toDouble() })
            check(
                "$s.stripTrailingZeros",
                { js.stripTrailingZeros() },
                { jvm.stripTrailingZeros() },
            )
            check("$s.abs", { js.abs() }, { jvm.abs() })
            check("$s.negate", { js.negate() }, { jvm.negate() })
            check("$s.toInt", { js.toInt() }, { jvm.toInt() })
            check("$s.toLong", { js.toLong() }, { jvm.toLong() })
            val n = random.nextInt(-5, 6)
            check(
                "$s.scaleByPowerOfTen($n)",
                { js.scaleByPowerOfTen(n) },
                { jvm.scaleByPowerOfTen(n) },
            )
            val p = random.nextInt(-1, 5)
            check("$s.pow($p)", { js.pow(p) }, { jvm.pow(p) })
            val scale = random.nextInt(-3, 12)
            check("$s.setScale($scale)", { js.setScale(scale) }, { jvm.setScale(scale) })
            for (mode in modes) {
                check(
                    "$s.setScale($scale, $mode)",
                    { js.setScale(scale, mode) },
                    { jvm.setScale(scale, java(mode)) },
                )
            }
        }
        mismatches.take(25).forEach(::println)
        assertEquals(0, mismatches.size, "mismatches with java.math.BigDecimal")
    }

    @Test
    fun binaryOperationsAgreeWithJava() {
        val mismatches = mutableListOf<String>()
        fun check(what: String, js: () -> Any?, jvm: () -> Any?) {
            val a = outcome(js)
            val b = outcome(jvm)
            if (a != b) mismatches += "$what: BigDecimalJs $a, java.math.BigDecimal $b"
        }
        val pool = values(600)
        for (i in 0 until 6000) {
            val x =
                if (i < fixed.size * fixed.size) fixed[i / fixed.size]
                else pool[random.nextInt(pool.size)]
            val y =
                if (i < fixed.size * fixed.size) fixed[i % fixed.size]
                else pool[random.nextInt(pool.size)]
            val a = BigDecimalJs(x)
            val b = BigDecimalJs(y)
            val c = java.math.BigDecimal(x)
            val d = java.math.BigDecimal(y)
            check("$x + $y", { a.add(b) }, { c.add(d) })
            check("$x - $y", { a.subtract(b) }, { c.subtract(d) })
            check("$x * $y", { a.multiply(b) }, { c.multiply(d) })
            check("$x / $y", { a.divide(b) }, { c.divide(d) })
            check("$x rem $y", { a.remainder(b) }, { c.remainder(d) })
            check(
                "$x divideAndRemainder $y",
                { a.divideAndRemainder(b) },
                { c.divideAndRemainder(d) },
            )
            check("$x compareTo $y", { a.compareTo(b) }, { c.compareTo(d) })
            check("$x equals $y", { a == b }, { c == d })
            val scale = random.nextInt(-2, 12)
            for (mode in modes) {
                check(
                    "$x / $y ($scale, $mode)",
                    { a.divide(b, scale, mode) },
                    { c.divide(d, scale, java(mode)) },
                )
            }
        }
        mismatches.take(25).forEach(::println)
        assertEquals(0, mismatches.size, "mismatches with java.math.BigDecimal")
    }

    @Test
    fun constructorsAgreeWithJava() {
        val mismatches = mutableListOf<String>()
        val doubles =
            listOf(
                0.0,
                -0.0,
                0.1,
                0.5,
                1.0,
                2.0,
                1e-300,
                123.456,
                -7.25,
                Double.MAX_VALUE,
                Double.MIN_VALUE,
                1.0 / 3.0,
            ) +
                List(2000) { random.nextDouble(-1e6, 1e6) } +
                List(500) { Double.fromBits(random.nextLong()) }
        for (v in doubles) {
            val a = outcome { BigDecimalJs(v) }
            val b = outcome { java.math.BigDecimal(v) }
            if (a != b) mismatches += "new($v): BigDecimalJs $a, java.math.BigDecimal $b"
        }
        val longs =
            listOf(0L, 1L, -1L, Long.MAX_VALUE, Long.MIN_VALUE) + List(500) { random.nextLong() }
        for (v in longs) {
            val a = outcome { BigDecimalJs(v) }
            val b = outcome { java.math.BigDecimal(v) }
            if (a != b) mismatches += "new($v L): BigDecimalJs $a, java.math.BigDecimal $b"
        }
        for (s in
            listOf(
                "",
                ".",
                "+",
                "-",
                "e5",
                "1e",
                "1.2.3",
                "abc",
                " 1",
                "1 ",
                "1_0",
                "--1",
                "1e+",
                ".e1",
                "0x10",
            )) {
            val a = outcome { BigDecimalJs(s) }
            val b = outcome { java.math.BigDecimal(s) }
            if (a != b) mismatches += "new(\"$s\"): BigDecimalJs $a, java.math.BigDecimal $b"
        }
        mismatches.take(25).forEach(::println)
        assertEquals(0, mismatches.size, "mismatches with java.math.BigDecimal")
    }
}
