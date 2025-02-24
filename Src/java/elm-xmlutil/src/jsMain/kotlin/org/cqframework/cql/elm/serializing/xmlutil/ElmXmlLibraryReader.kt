package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.io.Source
import kotlinx.io.readString
import nl.adaptivity.xmlutil.xmlStreaming
import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.hl7.elm.r1.Library

actual class ElmXmlLibraryReader actual constructor() : ElmLibraryReader {
    actual override fun read(string: String): Library {
        return xml.decodeFromReader(
            Library.serializer(),
            TypeInjectingXmlReader(xmlStreaming.newReader(string))
        )
    }

    actual override fun read(source: Source): Library {
        return read(source.readString())
    }
}
