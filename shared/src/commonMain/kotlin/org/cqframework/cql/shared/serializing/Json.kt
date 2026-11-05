@file:Suppress("PackageNaming")

package org.cqframework.cql.shared.serializing

import org.cqframework.cql.shared.QName

/**
 * Parses the qualified name from a JSON string. This is the reverse of the `QName.toString()`
 * method.
 *
 * @param value The JSON string to parse, which may include a namespace URI, e.g.
 *   "{urn:hl7-org:elm-types:r1}Integer".
 * @return The corresponding `QName` object.
 */
fun jsonStringToQName(value: String): QName {
    if (value.startsWith("{")) {
        val endIndex = value.indexOf("}")
        require(endIndex != -1) { "Invalid QName format: $value" }
        val namespaceURI = value.substring(1, endIndex)
        val localPart = value.substring(endIndex + 1)
        return QName(namespaceURI, localPart)
    }

    return QName(value)
}
