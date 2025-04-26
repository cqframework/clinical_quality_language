@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import kotlinx.io.Source
import kotlinx.io.readString
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.fromXmlElement

class XmlModelInfoReader : ModelInfoReader {
    override fun read(string: String): ModelInfo {
        val tree = parseXml(string)

        return ModelInfo.fromXmlElement(tree, emptyMap())
    }

    override fun read(source: Source): ModelInfo {
        return read(source.readString())
    }
}
