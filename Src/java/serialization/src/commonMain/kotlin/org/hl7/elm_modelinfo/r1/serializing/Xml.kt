@file:Suppress("PackageNaming", "MatchingDeclarationName")

package org.hl7.elm_modelinfo.r1.serializing

import org.cqframework.cql.elm.serializing.QName

sealed class XmlNode {
    data class Text(val text: String) : XmlNode()

    data class Element(
        val tagName: String,
        val attributes: Map<String, String>,
        val children: List<XmlNode>
    ) : XmlNode()
}

expect fun parseXml(xml: String): XmlNode.Element

expect fun toXmlString(element: XmlNode.Element, namespaces: Map<String, String>): String

fun xmlAttributeValueToQName(value: String, namespaces: Map<String, String>): QName {
    val parts = value.split(":")
    return if (parts.size == 2) {
        val prefix = parts[0]
        QName(namespaces[prefix] ?: "", parts[1], prefix)
    } else {
        QName(namespaces[""] ?: "", parts[0])
    }
}

fun getNewKey(namespaces: Map<String, String>): String {
    val prefix = "ns"
    var i = 0
    while (namespaces.containsKey("$prefix$i")) {
        i++
    }
    return "$prefix$i"
}

fun qNameToXmlAttributeValue(
    qname: QName,
    namespaces: MutableMap<String, String>,
    defaultNamespaces: Map<String, String>
): String {
    val localPart = qname.getLocalPart()
    val namespace = qname.getNamespaceURI()

    val prefix =
        namespaces.entries.find { it.value == namespace }?.key
            ?: defaultNamespaces.entries.find { it.value == namespace }?.key
            ?: getNewKey(namespaces)
    namespaces[prefix] = namespace

    return if (prefix.isEmpty()) {
        localPart
    } else {
        "$prefix:$localPart"
    }
}
