package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
sealed interface CqlType {
    val typeAsString: kotlin.String
}

data class Boolean(val value: kotlin.Boolean) : NamedCqlType {
    override val type = booleanTypeName

    override fun toString() = value.toString()

    companion object {
        val TRUE = Boolean(true)
        val FALSE = Boolean(false)
    }
}

data class Integer(val value: Int) : NamedCqlType {
    override val type = integerTypeName

    override fun toString() = value.toString()

    companion object {
        val ZERO = Integer(0)
        val ONE = Integer(1)
    }
}

data class Long(val value: kotlin.Long) : NamedCqlType {
    override val type = longTypeName

    override fun toString() = value.toString()
}

data class Decimal(val value: BigDecimal) : NamedCqlType {
    override val type = decimalTypeName

    override fun toString() = value.toString()
}

data class String(val value: kotlin.String) : NamedCqlType, CharSequence by value {
    override val type = stringTypeName

    override fun toString() = value

    companion object {
        val EMPTY_STRING = String("")
    }
}

// data class List<E : CqlType?>(val value: Iterable<E>) : CqlType, Iterable<E> by value
data class List(val value: Iterable<CqlType?>) : CqlType, Iterable<CqlType?> by value {
    override val typeAsString = "List"

    companion object {
        val EMPTY_LIST = List(emptyList())
    }
}

fun kotlin.Boolean.toCqlBoolean() = Boolean(this)

fun kotlin.Int.toCqlInteger() = Integer(this)

fun kotlin.Long.toCqlLong() = Long(this)

fun BigDecimal.toCqlDecimal() = Decimal(this)

fun kotlin.String.toCqlString() = String(this)

// fun <E: CqlType?> Iterable<E>.toCqlList() = List(this)
fun Iterable<CqlType?>.toCqlList() = List(this)
