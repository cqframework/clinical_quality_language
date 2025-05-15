package org.cqframework.cql.shared

import com.ionspin.kotlin.bignum.decimal.BigDecimal as KtBigDecimal

actual class BigDecimal {
    private val value: KtBigDecimal

    actual constructor(value: String) {
        this.value = KtBigDecimal.parseString(value)
    }

    actual constructor(value: Double) {
        this.value = KtBigDecimal.fromDouble(value)
    }

    actual fun toPlainString(): String {
        return this.value.toPlainString()
    }

    actual fun toDouble(): Double {
        return this.value.doubleValue(false)
    }
}
