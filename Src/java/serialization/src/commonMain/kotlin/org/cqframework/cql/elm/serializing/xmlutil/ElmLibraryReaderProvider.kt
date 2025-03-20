package org.cqframework.cql.elm.serializing.xmlutil

import org.cqframework.cql.elm.serializing.ElmLibraryReader

fun getElmLibraryReader(contentType: String): ElmLibraryReader {
    return when (contentType) {
        "application/elm+xml" -> ElmXmlLibraryReader()
        "application/elm+json" -> ElmJsonLibraryReader()
        else -> ElmXmlLibraryReader()
    }
}
