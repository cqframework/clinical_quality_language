package org.cqframework.cql.elm.serializing

import kotlinx.io.Sink
import org.hl7.elm.r1.Library

interface ElmLibraryWriter {
    fun write(library: Library, sink: Sink)

    fun writeAsString(library: Library): String
}
