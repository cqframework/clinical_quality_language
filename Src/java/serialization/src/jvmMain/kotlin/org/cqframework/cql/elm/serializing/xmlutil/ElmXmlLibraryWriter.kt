package org.cqframework.cql.elm.serializing.xmlutil

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import kotlinx.io.Sink
import kotlinx.io.asOutputStream
import nl.adaptivity.xmlutil.core.impl.newWriter
import nl.adaptivity.xmlutil.xmlStreaming
import org.cqframework.cql.elm.serializing.ElmLibraryWriter
import org.hl7.elm.r1.Library

actual class ElmXmlLibraryWriter actual constructor() : ElmLibraryWriter {
    fun writeToOutputStream(library: Library, outputStream: OutputStream) {
        xml.encodeToWriter(
            xmlStreaming.newWriter(EscapingOutputStream(outputStream), "UTF-8"),
            Library.serializer(),
            library
        )
    }

    actual override fun write(library: Library, sink: Sink) {
        writeToOutputStream(library, sink.asOutputStream())
    }

    actual override fun writeAsString(library: Library): String {
        val outputStream = ByteArrayOutputStream()
        writeToOutputStream(library, outputStream)
        return outputStream.toString("UTF-8")
    }
}

internal class EscapingOutputStream(private val outputStream: OutputStream) : OutputStream() {
    override fun write(b: Int) {
        // Escape characters like `\f` as `&#xc;`.
        // This is needed because StAX outputs these characters as is.
        @Suppress("MagicNumber")
        if (b < 0x20) {
            outputStream.write("&#x${b.toString(16)};".toByteArray())
        } else {
            outputStream.write(b)
        }
    }
}
