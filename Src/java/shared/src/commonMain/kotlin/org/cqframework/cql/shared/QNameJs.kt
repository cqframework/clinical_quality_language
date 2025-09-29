package org.cqframework.cql.shared

/** A minimal pure-Kotlin implementation of QName for non-Java environments. */
class QNameJs
constructor(
    private val namespaceURI: String,
    private val localPart: String,
    private val prefix: String,
) {
    constructor(namespaceURI: String, localPart: String) : this(namespaceURI, localPart, "")

    constructor(localPart: String) : this("", localPart, "")

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
