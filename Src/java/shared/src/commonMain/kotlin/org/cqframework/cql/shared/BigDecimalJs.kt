package org.cqframework.cql.shared

import com.ionspin.kotlin.bignum.decimal.BigDecimal as KtBigDecimal

/** A minimal pure-Kotlin implementation of BigDecimal for non-Java environments. */
class BigDecimalJs {
    private val value: KtBigDecimal

    constructor(value: String) {
        this.value = KtBigDecimal.parseString(value)
    }

    constructor(value: Double) {
        this.value = KtBigDecimal.fromDouble(value)
    }

    fun toPlainString(): String {
        return this.value.toPlainString()
    }

    fun toDouble(): Double {
        return this.value.doubleValue(false)
    }
}
