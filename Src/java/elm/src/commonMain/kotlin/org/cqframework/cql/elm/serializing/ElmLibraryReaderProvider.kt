package org.cqframework.cql.elm.serializing

interface ElmLibraryReaderProvider {
    fun create(contentType: String): ElmLibraryReader
}
