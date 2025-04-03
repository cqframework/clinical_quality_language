@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import kotlinx.io.Source
import kotlinx.io.asInputStream
import nl.adaptivity.xmlutil.core.impl.newReader
import nl.adaptivity.xmlutil.xmlStreaming
import org.hl7.elm_modelinfo.r1.ModelInfo

actual class XmlModelInfoReader actual constructor() : ModelInfoReader {
    actual override fun read(string: String): ModelInfo {
        return xml.decodeFromReader(
            ModelInfo.serializer(),
            TypeInjectingXmlReader(xmlStreaming.newReader(string))
        )
    }

    actual override fun read(source: Source): ModelInfo {
        return xml.decodeFromReader(
            ModelInfo.serializer(),
            TypeInjectingXmlReader(xmlStreaming.newReader(source.asInputStream(), "UTF-8"))
        )
    }
}
