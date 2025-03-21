package org.cqframework.cql.elm.serializing

fun getElmLibraryWriter(contentType: String): ElmLibraryWriter {
    return when (contentType) {
        "application/elm+xml" -> ElmXmlLibraryWriter()
        "application/elm+json" -> ElmJsonLibraryWriter()
        else -> ElmXmlLibraryWriter()
    }
}
