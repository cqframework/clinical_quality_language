@file:OptIn(ExperimentalWasmJsInterop::class)

package org.cqframework.cql.shared.serializing

import kotlin.js.ExperimentalWasmJsInterop
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.parsing.DOMParser

internal fun parseXmlUsingDomApi(xml: String): XmlNode.Element {
    val root = DOMParser().parseFromString(xml, "text/xml".toJsString()).documentElement!!
    return parseElement(root)
}

private fun parseElement(element: Element): XmlNode.Element {
    val children = mutableListOf<XmlNode>()
    for (i in 0 until element.childNodes.length) {
        val child = element.childNodes.item(i)
        when (child?.nodeType) {
            Node.TEXT_NODE -> children.add(XmlNode.Text(child.textContent ?: ""))
            Node.ELEMENT_NODE -> children.add(parseElement(child as Element))
        }
    }

    val attributes = mutableMapOf<String, String>()
    for (i in 0 until element.attributes.length) {
        val attr = element.attributes.item(i)!!
        attributes[attr.nodeName] = attr.nodeValue!!
    }

    return XmlNode.Element(tagName = element.tagName, attributes = attributes, children = children)
}
