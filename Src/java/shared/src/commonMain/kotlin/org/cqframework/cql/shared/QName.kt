@file:Suppress("UnusedPrivateProperty")

package org.cqframework.cql.shared

/** A minimal multiplatform implementation of QName. */
expect class QName {
    constructor(namespaceURI: String, localPart: String, prefix: String)

    constructor(namespaceURI: String, localPart: String)

    constructor(localPart: String)

    fun getPrefix(): String

    fun getLocalPart(): String

    fun getNamespaceURI(): String
}
