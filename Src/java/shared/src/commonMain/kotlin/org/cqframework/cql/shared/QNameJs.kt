package org.cqframework.cql.shared

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

/** A minimal pure-Kotlin implementation of QName for non-Java environments. */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@JsName("QName")
class QNameJs(
    private val namespaceURI: String,
    private val localPart: String,
    private val prefix: String,
) {
    @JsExport.Ignore
    constructor(namespaceURI: String, localPart: String) : this(namespaceURI, localPart, "")

    @JsExport.Ignore constructor(localPart: String) : this("", localPart, "")

    fun getPrefix(): String = prefix

    fun getLocalPart(): String = localPart

    fun getNamespaceURI(): String = namespaceURI

    override fun toString(): String {
        if (namespaceURI == "") return localPart
        return "{$namespaceURI}$localPart"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is QNameJs) {
            if (namespaceURI != other.namespaceURI) return false
            if (localPart != other.localPart) return false

            return true
        }

        return false
    }

    override fun hashCode(): Int {
        var result = namespaceURI.hashCode()
        result = 31 * result + localPart.hashCode()
        return result
    }
}
