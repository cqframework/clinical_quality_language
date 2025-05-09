package org.cqframework.cql.shared

expect class BigDecimal {
    constructor(@Suppress("UnusedPrivateProperty") value: String)

    constructor(@Suppress("UnusedPrivateProperty") value: Double)

    fun toPlainString(): String

    fun toDouble(): Double
}
