package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

/** Represents a structured CQL value. */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
sealed class StructuredValue : Value {
    abstract val elements: MutableMap<kotlin.String, Value?>

    /** Returns true if the structured value has an element with the given name, false otherwise. */
    fun has(elementName: kotlin.String): kotlin.Boolean {
        return elements.containsKey(elementName)
    }

    /** Returns the value of the element of the structured value. */
    operator fun get(elementName: kotlin.String): Value? {
        return elements[elementName]
    }

    /** Returns the value of the element of the structured value. */
    fun getElement(elementName: kotlin.String): Value? {
        return elements[elementName]
    }

    override fun equals(other: Any?): kotlin.Boolean {
        if (this === other) return true
        if (other !is StructuredValue) return false

        if (this is NamedTypeValue) {
            if (other !is NamedTypeValue) return false
            if (type != other.type) return false
        }

        if (elements != other.elements) return false

        return true
    }

    override fun hashCode(): Int {
        var result = if (this is NamedTypeValue) type.hashCode() else 0
        result = 31 * result + elements.hashCode()
        return result
    }

    override fun toString(): kotlin.String {
        if (elements.isEmpty()) {
            return "$typeAsString { : }"
        }

        return "$typeAsString {\n" +
            elements.entries.joinToString(",\n") { "${it.key}: ${it.value}".prependIndent("  ") } +
            "\n}"
    }
}
