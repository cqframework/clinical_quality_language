package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName

/** Represents an instance of a named structured type (class). */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
data class CqlClassInstance(val type: QName, val elements: MutableMap<String, Any?>) : CqlType {
    /** Returns the value of the element of the structured type. */
    operator fun get(elementName: String): Any? {
        return elements[elementName]
    }

    /** Returns true if the structured type has an element with the given name, false otherwise. */
    fun has(elementName: String): Boolean {
        return elements.containsKey(elementName)
    }
}
