@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import kotlinx.io.Source
import kotlinx.io.readString
import org.cqframework.cql.shared.serializing.parseXml
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.fromXmlElement

fun parseModelInfoXml(string: String): ModelInfo {
    val tree = parseXml(string)
    return ModelInfo.fromXmlElement(tree, emptyMap())
}

fun parseModelInfoXml(source: Source): ModelInfo {
    return parseModelInfoXml(source.readString())
}
