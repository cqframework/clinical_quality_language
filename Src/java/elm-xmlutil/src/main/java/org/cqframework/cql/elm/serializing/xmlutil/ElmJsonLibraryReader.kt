@file:Suppress("detekt:all")

package org.cqframework.cql.elm.serializing.xmlutil

import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URI
import java.net.URL
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.hl7.elm.r1.Library

class ElmJsonLibraryReader : ElmLibraryReader {
    override fun read(file: File): Library {
        file.inputStream().use {
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
        return Json.decodeFromString(Library.serializer(), string)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun read(inputStream: InputStream): Library {
        return Json.decodeFromStream<Library>(inputStream)
    }

    override fun read(reader: Reader): Library {
        val str = reader.readText()
        return read(str)
    }
}
