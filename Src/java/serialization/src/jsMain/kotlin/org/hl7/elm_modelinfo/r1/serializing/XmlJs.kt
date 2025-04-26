@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import kotlinx.browser.document
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.parsing.DOMParser
import org.w3c.dom.parsing.XMLSerializer

actual fun parseXml(xml: String): XmlNode.Element {
    val root = DOMParser().parseFromString(xml, "text/xml").documentElement!!
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

actual fun toXmlString(element: XmlNode.Element, namespaces: Map<String, String>): String {
    val doc =
        document.implementation.createDocument(
            namespaces[element.tagName.substringBefore(":", "")],
            element.tagName
        )
    val documentElement = doc.documentElement!!
    for ((prefix, uri) in namespaces) {
        documentElement.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            if (prefix.isEmpty()) "xmlns" else "xmlns:$prefix",
            uri
        )
    }
    exportDomContent(doc, documentElement, element, namespaces)

    return XMLSerializer().serializeToString(doc)
}

private fun exportDomContent(
    doc: Document,
    domElement: Element,
    element: XmlNode.Element,
    namespaces: Map<String, String>
) {
    for ((name, value) in element.attributes) {
        domElement.setAttribute(name, value)
    }

    for (child in element.children) {
        val childNode =
            when (child) {
                is XmlNode.Text -> doc.createTextNode(child.text)
                is XmlNode.Element -> {
                    val childElement =
                        doc.createElementNS(
                            namespaces[child.tagName.substringBefore(":", "")],
                            child.tagName
                        )
                    exportDomContent(doc, childElement, child, namespaces)
                    childElement
                }
            }
        domElement.appendChild(childNode)
    }
}
