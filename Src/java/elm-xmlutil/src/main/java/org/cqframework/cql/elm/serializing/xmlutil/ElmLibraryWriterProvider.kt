package org.cqframework.cql.elm.serializing.xmlutil

import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.cqframework.cql.elm.serializing.ElmLibraryWriterProvider

class ElmLibraryWriterProvider : ElmLibraryWriterProvider {
    override fun create(contentType: String): ElmLibraryWriter {
        var contentType: String? = contentType
        if (contentType == null) {
            contentType = "application/elm+json"
        }
        return when (contentType) {
            "application/elm+xml" -> ElmXmlLibraryWriter()
            "application/elm+json" -> ElmJsonLibraryWriter()
            else -> ElmXmlLibraryWriter()
        }
    }
}
