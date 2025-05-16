package org.cqframework.cql.shared

/** A minimal multiplatform implementation of BigDecimal. */
expect class BigDecimal {
    constructor(@Suppress("UnusedPrivateProperty") value: String)

    constructor(@Suppress("UnusedPrivateProperty") value: Double)

    fun toPlainString(): String

    fun toDouble(): Double
}
