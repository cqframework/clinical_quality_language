package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.io.Sink
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

expect class ElmXmlLibraryWriter() : ElmLibraryWriter {
    override fun write(library: Library, sink: Sink)

    override fun writeAsString(library: Library): String
}
