package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport

/** Represents a structured CQL value. */
@JsOnlyExport
sealed interface StructuredValue : Value {
    val elements: Map<kotlin.String, Value?>

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

    fun toStructuredValueString(): kotlin.String {
        if (elements.isEmpty()) {
            return "$typeAsString { : }"
        }

        return "$typeAsString {\n" +
            elements.entries.joinToString(",\n") { "${it.key}: ${it.value}".prependIndent("  ") } +
            "\n}"
    }
}
