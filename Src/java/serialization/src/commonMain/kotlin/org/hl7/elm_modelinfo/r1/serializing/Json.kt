@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import org.cqframework.cql.elm.serializing.QName

/**
 * Parses the qualified name from a JSON string. This is the reverse of the `QName.toString()`
 * method.
 *
 * @param value The JSON string to parse, which may include a namespace URI, e.g.
 *   "{urn:hl7-org:elm-types:r1}Integer".
 * @return The corresponding `QName` object.
 */
internal fun jsonStringToQName(value: String): QName {
    if (value.startsWith("{")) {
        val parts = value.substring(1).split("}")
        return QName(parts[0], parts[1])
    }

    return QName(value)
}
