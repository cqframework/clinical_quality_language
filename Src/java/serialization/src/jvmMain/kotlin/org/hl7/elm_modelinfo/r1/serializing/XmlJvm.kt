@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import java.io.ByteArrayInputStream
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

actual fun parseXml(xml: String): XmlNode.Element {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val root = builder.parse(ByteArrayInputStream(xml.toByteArray())).getDocumentElement()

    return parseElement(root)
}

private fun parseElement(element: Element): XmlNode.Element {
    val children = mutableListOf<XmlNode>()
    for (i in 0 until element.childNodes.length) {
        val child = element.childNodes.item(i)
        when (child.nodeType) {
            Node.TEXT_NODE -> children.add(XmlNode.Text(child.textContent))
            Node.ELEMENT_NODE -> children.add(parseElement(child as Element))
        }
    }

    val attributes = mutableMapOf<String, String>()
    for (i in 0 until element.attributes.length) {
        val attr = element.attributes.item(i)
        attributes[attr.nodeName] = attr.nodeValue
    }

    return XmlNode.Element(tagName = element.tagName, attributes = attributes, children = children)
}

actual fun toXmlString(element: XmlNode.Element, namespaces: Map<String, String>): String {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val doc = builder.newDocument()

    val documentElement =
        doc.createElementNS(namespaces[element.tagName.substringBefore(":", "")], element.tagName)
    for ((prefix, uri) in namespaces) {
        documentElement.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            if (prefix.isEmpty()) "xmlns" else "xmlns:$prefix",
            uri
        )
    }
    exportDomContent(doc, documentElement, element, namespaces)
    doc.appendChild(documentElement)

    val transformer = TransformerFactory.newInstance().newTransformer()
    val writer = StringWriter()
    transformer.transform(DOMSource(doc), StreamResult(writer))
    return writer.toString()
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
