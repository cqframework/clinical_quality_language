package org.cqframework.cql.elm.serializing

import kotlinx.io.Sink
import kotlinx.io.writeString
import kotlinx.serialization.json.buildJsonObject
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.toJsonObject

class ElmJsonLibraryWriter : ElmLibraryWriter {
    override fun write(library: Library, sink: Sink) {
        sink.writeString(writeAsString(library))
    }

    override fun writeAsString(library: Library): String {
        return buildJsonObject { put("library", library.toJsonObject(false)) }.toString()
    }
}
