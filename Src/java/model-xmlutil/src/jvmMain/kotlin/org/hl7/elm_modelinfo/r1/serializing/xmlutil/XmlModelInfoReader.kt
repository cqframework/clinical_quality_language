package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import kotlinx.io.Source
import kotlinx.io.asInputStream
import nl.adaptivity.xmlutil.core.impl.newReader
import nl.adaptivity.xmlutil.xmlStreaming
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader

actual class XmlModelInfoReader actual constructor() : ModelInfoReader {
    actual override fun read(string: String): ModelInfo {
        return xml.decodeFromString(ModelInfo.serializer(), string)
    }

    actual override fun read(source: Source): ModelInfo {
        return xml.decodeFromReader(
            ModelInfo.serializer(),
            xmlStreaming.newReader(source.asInputStream(), "UTF-8")
        )
    }
}
