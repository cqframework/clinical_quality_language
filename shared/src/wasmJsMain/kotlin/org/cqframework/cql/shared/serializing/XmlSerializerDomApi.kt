package org.cqframework.cql.shared.serializing

import kotlinx.browser.document
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.parsing.XMLSerializer

internal fun serializeUsingDomApi(
    element: XmlNode.Element,
    namespaces: Map<String, String>,
): String {
    val doc =
        document.implementation.createDocument(
            namespaces[element.tagName.substringBefore(":", "")],
            element.tagName,
        )
    val documentElement = doc.documentElement!!
    for ((prefix, uri) in namespaces) {
        documentElement.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            if (prefix.isEmpty()) "xmlns" else "xmlns:$prefix",
            uri,
        )
    }
    exportDomContent(doc, documentElement, element, namespaces)

    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + XMLSerializer().serializeToString(doc)
}

private fun exportDomContent(
    doc: Document,
    domElement: Element,
    element: XmlNode.Element,
    namespaces: Map<String, String>,
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
                            child.tagName,
                        )
                    exportDomContent(doc, childElement, child, namespaces)
                    childElement
                }
            }
        domElement.appendChild(childNode)
    }
}
