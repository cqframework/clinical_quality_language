package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class List(val value: Iterable<Value?>) : Value, Iterable<Value?> by value {
    override val typeAsString = "List"

    companion object {
        val EMPTY_LIST = List(emptyList())
    }

    private val hash: Int by lazy { value.hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other || (other is List && value == other.value)

    override fun toString(): kotlin.String {
        if (value.count() == 0) {
            return "{}"
        }

        return "{\n" + value.joinToString(",\n") { "$it".prependIndent("  ") } + "\n}"
    }
}

fun Iterable<Value?>.toCqlList() = List(this)
