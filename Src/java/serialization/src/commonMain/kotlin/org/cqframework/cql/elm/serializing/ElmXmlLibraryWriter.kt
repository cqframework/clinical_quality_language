package org.cqframework.cql.elm.serializing

import kotlinx.io.Sink
import org.hl7.elm.r1.Library

expect class ElmXmlLibraryWriter() : ElmLibraryWriter {
    override fun write(library: Library, sink: Sink)

    override fun writeAsString(library: Library): String
}
