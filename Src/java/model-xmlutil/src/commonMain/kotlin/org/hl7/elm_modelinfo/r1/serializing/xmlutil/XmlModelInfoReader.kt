package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import kotlinx.io.Source
import kotlinx.io.readString
import nl.adaptivity.xmlutil.serialization.XML
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader

private val xml = XML(org.hl7.elm_modelinfo.r1.serializersModule)

class XmlModelInfoReader : ModelInfoReader {
    override fun read(string: String): ModelInfo {
        return xml.decodeFromString(
            ModelInfo.serializer(),
            string
        )
    }
    override fun read(source: Source): ModelInfo {
        return read(source.readString())
    }
}
