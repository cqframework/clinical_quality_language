package org.cqframework.cql.elm.serializing.xmlutil

import java.io.IOException
import java.io.Writer
import kotlinx.serialization.json.Json
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

/**
 * Implementation of an ELM XML serializer using the Jackson serialization framework. This
 * implementation is known non-functional, but after 3 different devs fiddling with it for untold
 * frustrating hours, we are abandoning it for now as a use case we don't care about anyway
 */
class ElmJsonLibraryWriter : ElmLibraryWriter {
    @Throws(IOException::class)
    override fun write(library: Library, writer: Writer) {
        writer.write(writeAsString(library))
    }

    override fun writeAsString(library: Library): String {
        return Json.encodeToString(Library.serializer(), library)
    }
}
