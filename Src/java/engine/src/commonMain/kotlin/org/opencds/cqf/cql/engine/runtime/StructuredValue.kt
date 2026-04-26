package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.elm.executing.ToStringEvaluator.toString

/** Represents a structured CQL value. */
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

    /** Returns a string representation of the elements of the structured value. */
    protected fun toPrettyString(label: kotlin.String): kotlin.String {
        if (elements.isEmpty()) {
            return "$label {}"
        }

        return buildString {
            appendLine("$label {")
            for ((key, value) in elements) {
                // append valueString and indent its every line
                appendLine("$key: ${toString(value)}".prependIndent("  "))
            }
            append("}")
        }
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
}
