package org.cqframework.cql.elm.serializing.xmlutil

import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.cqframework.cql.elm.serializing.ElmLibraryReaderProvider

class ElmLibraryReaderProvider : ElmLibraryReaderProvider {
    override fun create(contentType: String): ElmLibraryReader {
        var contentType: String? = contentType
        if (contentType == null) {
            contentType = "application/elm+json"
        }
        return when (contentType) {
            "application/elm+xml" -> ElmXmlLibraryReader()
            "application/elm+json" -> ElmJsonLibraryReader()
            else -> ElmXmlLibraryReader()
        }
    }
}
