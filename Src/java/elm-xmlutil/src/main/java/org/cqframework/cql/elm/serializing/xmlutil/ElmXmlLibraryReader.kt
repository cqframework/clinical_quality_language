@file:Suppress("detekt:all")

package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.overwriteWith
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URI
import java.net.URL
import kotlinx.serialization.modules.plus
import nl.adaptivity.xmlutil.QNameSerializer
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
//        serializersModule.overwriteWith(SerializersModule { contextual(QNameSerializer) })
        val xml =
            XML(serializersModule) {
                //                            autoPolymorphic = true
                //                defaultPolicy {
                //                    typeDiscriminatorName =
                // QName("http://www.w3.org/2001/XMLSchema-instance", "type")
                //                }
            }

        val library = xml.decodeFromReader(Library.serializer(), xmlStreaming.newReader(reader))

        return library
    }
}
