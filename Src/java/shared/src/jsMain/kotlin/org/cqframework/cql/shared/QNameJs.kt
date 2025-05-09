@file:Suppress("MatchingDeclarationName")

package org.cqframework.cql.shared

actual class QName
actual constructor(
    private val namespaceURI: String,
    private val localPart: String,
    private val prefix: String
) {
    actual constructor(namespaceURI: String, localPart: String) : this(namespaceURI, localPart, "")

    actual constructor(localPart: String) : this("", localPart, "")

    actual fun getPrefix(): String = prefix

    actual fun getLocalPart(): String = localPart

    actual fun getNamespaceURI(): String = namespaceURI

    override fun toString(): String {
        if (namespaceURI == "") return localPart
        return "{$namespaceURI}$localPart"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is QName) {
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
