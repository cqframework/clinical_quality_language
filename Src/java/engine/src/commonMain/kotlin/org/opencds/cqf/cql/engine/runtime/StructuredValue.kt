package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.elm.executing.ToStringEvaluator.toString

/** Represents a structured CQL value. */
sealed class StructuredValue : CqlType {
    abstract val elements: MutableMap<String, Any?>

    /** Returns true if the structured value has an element with the given name, false otherwise. */
    fun has(elementName: String): Boolean {
        return elements.containsKey(elementName)
    }

    /** Returns the value of the element of the structured value. */
    operator fun get(elementName: String): Any? {
        return elements[elementName]
    }

    /** Returns the value of the element of the structured value. */
    fun getElement(elementName: String): Any? {
        return elements[elementName]
    }

    /** Returns a string representation of the elements of the structured value. */
    protected fun toPrettyString(label: String): String {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StructuredValue) return false

        if (this is NamedCqlType) {
            if (other !is NamedCqlType) return false
            if (type != other.type) return false
        }

        if (elements != other.elements) return false

        return true
    }

    override fun hashCode(): Int {
        var result = if (this is NamedCqlType) type.hashCode() else 0
        result = 31 * result + elements.hashCode()
        return result
    }
}
