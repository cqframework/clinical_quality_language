package org.cqframework.cql.elm.serializing

import kotlinx.io.Source
import kotlinx.io.asInputStream
import nl.adaptivity.xmlutil.core.impl.newReader
import nl.adaptivity.xmlutil.xmlStreaming
import org.hl7.elm.r1.Library
import org.hl7.elm_modelinfo.r1.serializing.TypeInjectingXmlReader

actual class ElmXmlLibraryReader actual constructor() : ElmLibraryReader {
    actual override fun read(string: String): Library {
        return xml.decodeFromReader(
            Library.serializer(),
            TypeInjectingXmlReader(xmlStreaming.newReader(string))
        )
    }

    actual override fun read(source: Source): Library {
        return xml.decodeFromReader(
            Library.serializer(),
            TypeInjectingXmlReader(xmlStreaming.newReader(source.asInputStream(), "UTF-8"))
        )
    }
}
