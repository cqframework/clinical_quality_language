package org.cqframework.cql.elm.serializing

class DefaultElmLibraryReaderProvider : ElmLibraryReaderProvider {
    override fun create(contentType: String): ElmLibraryReader {
        return when (contentType) {
            "application/elm+xml" -> ElmXmlLibraryReader()
            "application/elm+json" -> ElmJsonLibraryReader()
            else -> ElmXmlLibraryReader()
        }
    }
}
