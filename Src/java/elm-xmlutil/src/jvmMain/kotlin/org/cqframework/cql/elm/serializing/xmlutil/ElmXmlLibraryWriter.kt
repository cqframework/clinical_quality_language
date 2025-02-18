package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.io.Sink
import kotlinx.io.asOutputStream
import nl.adaptivity.xmlutil.core.impl.newWriter
import nl.adaptivity.xmlutil.xmlStreaming
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

actual class ElmXmlLibraryWriter actual constructor() : ElmLibraryWriter {
    actual override fun write(library: Library, sink: Sink) {
        xml.encodeToWriter(
            xmlStreaming.newWriter(sink.asOutputStream(), "UTF-8"),
            Library.serializer(),
            library
        )
    }

    actual override fun writeAsString(library: Library): String {
        return xml.encodeToString(Library.serializer(), library)
    }
}
