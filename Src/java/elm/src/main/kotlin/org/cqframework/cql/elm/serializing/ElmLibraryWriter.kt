package org.cqframework.cql.elm.serializing

import java.io.IOException
import java.io.Writer
import org.hl7.elm.r1.Library

interface ElmLibraryWriter {
    @Throws(IOException::class) fun write(library: Library, writer: Writer)

    fun writeAsString(library: Library): String
}
