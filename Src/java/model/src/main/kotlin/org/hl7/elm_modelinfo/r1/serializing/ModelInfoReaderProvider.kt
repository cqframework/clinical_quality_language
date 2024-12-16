@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

interface ModelInfoReaderProvider {
    fun create(contentType: String): ModelInfoReader?
}
