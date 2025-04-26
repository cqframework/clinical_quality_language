@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import org.cqframework.cql.elm.serializing.QName

// The string can be formatted as "{namespaceURI}localPart" or just "localPart"
fun jsonStringToQName(value: String): QName {
    if (value.startsWith("{")) {
        val parts = value.substring(1).split("}")
        return QName(parts[0], parts[1])
    }

    return QName(value)
}
