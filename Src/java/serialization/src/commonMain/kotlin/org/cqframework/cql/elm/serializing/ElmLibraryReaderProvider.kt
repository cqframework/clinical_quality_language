package org.cqframework.cql.elm.serializing

fun getElmLibraryReader(contentType: String): ElmLibraryReader {
    return when (contentType) {
        "application/elm+xml" -> ElmXmlLibraryReader()
        "application/elm+json" -> ElmJsonLibraryReader()
        else -> ElmXmlLibraryReader()
    }
}
