@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReader
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderProvider

class ModelInfoReaderProvider : ModelInfoReaderProvider {
    override fun create(contentType: String): ModelInfoReader {
        return when (contentType) {
            "application/xml" -> XmlModelInfoReader()
            else ->
                @Suppress("TooGenericExceptionThrown")
                throw RuntimeException("ModelInfo reader content type $contentType not supported")
        }
    }
}
