package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import kotlinx.io.Source
import nl.adaptivity.xmlutil.serialization.XML
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader

internal val xml = XML(org.hl7.elm_modelinfo.r1.serializersModule)

expect class XmlModelInfoReader() : ModelInfoReader {
    override fun read(string: String): ModelInfo

    override fun read(source: Source): ModelInfo
}
