package org.cqframework.cql.elm.serializing.xmlutil

import java.io.Writer
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

class ElmXmlLibraryWriter : ElmLibraryWriter {
    override fun write(library: Library, writer: Writer) {
        writer.write(writeAsString(library))
    }

    override fun writeAsString(library: Library): String {

        return xml.encodeToString(Library.serializer(), library)
    }
}
