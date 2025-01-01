@file:Suppress("detekt:all")

package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import kotlinx.io.Source
import kotlinx.io.asInputStream
import kotlinx.io.buffered
import nl.adaptivity.xmlutil.newReader
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.xmlStreaming
import org.hl7.elm_modelinfo.r1.*
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader

class XmlModelInfoReader : ModelInfoReader {
    override fun read(source: Source): ModelInfo {
        val serializersModule = Serializer.createSerializer()
        val xml = XML(serializersModule)
        val modelInfo =
            xml.decodeFromReader(
                ModelInfo.serializer(),
                xmlStreaming.newReader(source.buffered().asInputStream())
            )

        return modelInfo
    }
}
