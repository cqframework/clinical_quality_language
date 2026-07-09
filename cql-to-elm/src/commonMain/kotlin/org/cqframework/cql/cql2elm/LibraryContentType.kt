package org.cqframework.cql.cql2elm

/** This enum lists all the encodings for CQL libraries */
enum class LibraryContentType(private val mimeType: String) : MimeType {
    CQL("text/cql"),
    XML("application/elm+xml"),
    JSON("application/elm+json"),
    COFFEE("application/elm+coffee");

    override fun mimeType(): String {
        return mimeType
    }
}
