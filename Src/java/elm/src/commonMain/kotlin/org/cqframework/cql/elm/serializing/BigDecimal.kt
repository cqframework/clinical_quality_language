package org.cqframework.cql.elm.serializing

expect class BigDecimal {
    constructor(value: String)

    constructor(value: Double)

    fun toPlainString(): String

    fun toDouble(): Double
}
