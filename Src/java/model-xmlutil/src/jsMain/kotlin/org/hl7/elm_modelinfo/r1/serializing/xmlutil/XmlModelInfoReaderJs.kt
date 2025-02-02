package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import kotlinx.io.Source
import kotlinx.io.readString
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader

actual class XmlModelInfoReader actual constructor() : ModelInfoReader {
    actual override fun read(string: String): ModelInfo {
        return xml.decodeFromString(
            ModelInfo.serializer(),
            string
        )
    }
    actual override fun read(source: Source): ModelInfo {
        return read(source.readString())
    }
}
