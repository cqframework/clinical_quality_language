package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.BigDecimal

sealed interface SimpleValue : NamedTypeValue

data class Boolean(val value: kotlin.Boolean) : SimpleValue {
    override val type = booleanTypeName

    override fun toString() = value.toString()

    companion object {
        val TRUE = Boolean(true)
        val FALSE = Boolean(false)
    }
}

data class Integer(val value: Int) : SimpleValue {
    override val type = integerTypeName

    override fun toString() = value.toString()

    companion object {
        val ZERO = Integer(0)
        val ONE = Integer(1)
    }
}

data class Long(val value: kotlin.Long) : SimpleValue {
    override val type = longTypeName

    override fun toString() = value.toString()
}

data class Decimal(val value: BigDecimal) : SimpleValue {
    override val type = decimalTypeName

    override fun toString() = value.toString()
}

data class String(val value: kotlin.String) : SimpleValue, CharSequence by value {
    override val type = stringTypeName

    override fun toString() = value

    companion object {
        val EMPTY_STRING = String("")
    }
}

fun kotlin.Boolean.toCqlBoolean() = Boolean(this)

fun kotlin.Int.toCqlInteger() = Integer(this)

fun kotlin.Long.toCqlLong() = Long(this)

fun BigDecimal.toCqlDecimal() = Decimal(this)

fun kotlin.String.toCqlString() = String(this)
