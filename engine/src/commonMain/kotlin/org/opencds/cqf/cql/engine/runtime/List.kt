package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class List(val value: Iterable<Value?>) : Value, Iterable<Value?> by value {
    override val typeAsString = "List"

    companion object {
        val EMPTY_LIST = List(emptyList())
    }

    // Computed on first use; 0 means not yet computed (a computed 0 is just recomputed). Not a
    // lazy delegate, which would retain a lambda until first use.
    private var hash = 0

    override fun hashCode(): Int {
        var result = hash
        if (result == 0) {
            result = value.hashCode()
            hash = result
        }
        return result
    }

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
