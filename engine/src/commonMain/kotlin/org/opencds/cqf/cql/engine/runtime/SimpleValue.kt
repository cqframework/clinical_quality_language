package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.cql2elm.StringEscapeUtils.escapeCql
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport sealed interface SimpleValue : NamedTypeValue

@JsOnlyExport
class Boolean(val value: kotlin.Boolean) : SimpleValue {
    override val type = booleanTypeName

    private val hash: Int by lazy { value.hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Boolean && value == other.value)

    override fun toString() = value.toString()

    companion object {
        val TRUE = Boolean(true)
        val FALSE = Boolean(false)
    }
}

@JsOnlyExport
class Integer(val value: Int) : SimpleValue {
    override val type = integerTypeName

    private val hash: Int by lazy { value.hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Integer && value == other.value)

    override fun toString() = value.toString()

    companion object {
        val ZERO = Integer(0)
        val ONE = Integer(1)
    }
}

@JsOnlyExport
class Long(val value: kotlin.Long) : SimpleValue {
    override val type = longTypeName

    private val hash: Int by lazy { value.hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Long && value == other.value)

    override fun toString() = "${value}L"
}

@JsOnlyExport
class Decimal(val value: BigDecimal) : SimpleValue {
    override val type = decimalTypeName

    private val hash: Int by lazy { value.hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is Decimal && value == other.value)

    override fun toString() = value.toPlainString()
}

@JsOnlyExport
class String(val value: kotlin.String) : SimpleValue, CharSequence by value {
    override val type = stringTypeName

    private val hash: Int by lazy { value.hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is String && value == other.value)

    override fun toString() = "'${escapeCql(value)}'"

    companion object {
        val EMPTY_STRING = String("")
    }
}

fun kotlin.Boolean.toCqlBoolean() = Boolean(this)

fun kotlin.Int.toCqlInteger() = Integer(this)

fun kotlin.Long.toCqlLong() = Long(this)

fun BigDecimal.toCqlDecimal() = Decimal(this)

fun kotlin.String.toCqlString() = String(this)
