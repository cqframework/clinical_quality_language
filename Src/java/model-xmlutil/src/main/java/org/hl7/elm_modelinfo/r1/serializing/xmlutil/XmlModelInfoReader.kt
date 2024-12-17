@file:Suppress("detekt:all")

package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URI
import java.net.URL
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus
import nl.adaptivity.xmlutil.QNameSerializer
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.xmlStreaming
import org.hl7.elm_modelinfo.r1.*
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader

class XmlModelInfoReader : ModelInfoReader {
    override fun read(src: File): ModelInfo {
        src.reader().use {
            return read(it)
        }
    }

    override fun read(src: Reader): ModelInfo {
//        val serializersModule =
//            SerializersModule { contextual(QNameSerializer) } + Serializer.createSerializer()
        val serializersModule = Serializer.createSerializer()
        val xml =
            XML(serializersModule) {
                //            autoPolymorphic = true
                //                defaultPolicy {
                //                    typeDiscriminatorName =
                // QName("http://www.w3.org/2001/XMLSchema-instance", "type")
                //                }
            }

        val modelInfo = xml.decodeFromReader(ModelInfo.serializer(), xmlStreaming.newReader(src))

        return modelInfo
    }

    override fun read(src: InputStream): ModelInfo {
        src.reader().use {
            return read(it)
        }
    }

    override fun read(url: URL): ModelInfo {
        url.openStream().use {
            return read(it)
        }
    }

    override fun read(uri: URI): ModelInfo {
        uri.toURL().openStream().use {
            return read(it)
        }
    }

    override fun read(string: String): ModelInfo {
        string.reader().use {
            return read(it)
        }
    }
}
