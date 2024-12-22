@file:Suppress("detekt:all")

package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderProvider

class ModelInfoReaderProvider : ModelInfoReaderProvider {
    override fun create(contentType: String): ModelInfoReader? {
        var contentType: String? = contentType
        if (contentType == null) {
            contentType = "application/xml"
        }
        return when (contentType) {
            "application/xml" -> XmlModelInfoReader()
            else ->
                throw RuntimeException(
                    String.format("ModelInfo reader content type %s not supported", contentType)
                )
        }
    }
}
