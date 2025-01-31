package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.io.Sink
import kotlinx.io.writeString
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

class ElmJsonLibraryWriter : ElmLibraryWriter {
    override fun write(library: Library, sink: Sink) {
        sink.writeString(writeAsString(library))
    }

    override fun writeAsString(library: Library): String {
        return json.encodeToString(LibraryWrapper.serializer(), LibraryWrapper(library))
    }
}
