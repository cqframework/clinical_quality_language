package org.cqframework.cql.elm.serializing

class DefaultElmLibraryWriterProvider : ElmLibraryWriterProvider {
    override fun create(contentType: String): ElmLibraryWriter {
        return when (contentType) {
            "application/elm+xml" -> ElmXmlLibraryWriter()
            "application/elm+json" -> ElmJsonLibraryWriter()
            else -> ElmXmlLibraryWriter()
        }
    }
}
