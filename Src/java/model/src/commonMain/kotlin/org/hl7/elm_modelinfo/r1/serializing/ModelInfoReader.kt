@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing
import kotlinx.io.Source
import org.hl7.elm_modelinfo.r1.ModelInfo

interface ModelInfoReader {
    fun read(string: String): ModelInfo
    fun read(source: Source): ModelInfo
}
