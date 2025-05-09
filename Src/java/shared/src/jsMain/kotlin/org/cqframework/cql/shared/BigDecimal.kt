package org.cqframework.cql.shared

import org.gciatto.kt.math.BigDecimal as KtBigDecimal

actual class BigDecimal {
    private val value: KtBigDecimal

    actual constructor(value: String) {
        this.value = KtBigDecimal.of(value)
    }

    actual constructor(value: Double) {
        this.value = KtBigDecimal.of(value)
    }

    actual fun toPlainString(): String {
        return this.value.toPlainString()
    }

    actual fun toDouble(): Double {
        return this.value.toDouble()
    }
}
