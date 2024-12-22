@file:Suppress("detekt:all")

package org.cqframework.cql.elm.serializing.xmlutil

import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URI
import java.net.URL
import kotlinx.serialization.modules.plus
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.xmlStreaming
import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.hl7.elm.r1.Library

class ElmXmlLibraryReader : ElmLibraryReader {
    override fun read(file: File): Library {
        file.reader().use {
            return read(it)
        }
    }

    override fun read(url: URL): Library {
        url.openStream().use {
            return read(it)
        }
    }

    override fun read(uri: URI): Library {
        uri.toURL().openStream().use {
            return read(it)
        }
    }

    override fun read(string: String): Library {
        string.reader().use {
            return read(it)
        }
    }

    override fun read(inputStream: InputStream): Library {
        inputStream.reader().use {
            return read(it)
        }
    }

    override fun read(reader: Reader): Library {
        val serializersModule =
            org.hl7.elm.r1.Serializer.createSerializer() +
                org.hl7.cql_annotations.r1.Serializer.createSerializer()
        val xml = XML(serializersModule)

        return xml.decodeFromReader(Library.serializer(), xmlStreaming.newReader(reader))
    }
}
