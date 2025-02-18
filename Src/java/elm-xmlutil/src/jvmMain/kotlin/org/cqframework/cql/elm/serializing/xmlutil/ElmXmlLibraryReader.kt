package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.io.Source
import kotlinx.io.asInputStream
import nl.adaptivity.xmlutil.core.impl.newReader
import nl.adaptivity.xmlutil.xmlStreaming
import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.hl7.elm.r1.Library

actual class ElmXmlLibraryReader actual constructor() : ElmLibraryReader {
    actual override fun read(string: String): Library {
        return xml.decodeFromString(Library.serializer(), string)
    }

    actual override fun read(source: Source): Library {
        return xml.decodeFromReader(
            Library.serializer(),
            xmlStreaming.newReader(source.asInputStream(), "UTF-8")
        )
    }
}
