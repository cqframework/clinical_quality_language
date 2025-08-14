package org.cqframework.cql.shared.serializing

/** Serializes an XML document to a string. Used in JS when the DOM API is not available. */
internal fun serializeUsingPolyfill(
    element: XmlNode.Element,
    namespaces: Map<String, String>
): String {
    val sb = StringBuilder()
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")

    val elementWithXmlnsAttributes =
        element.copy(
            attributes =
                namespaces
                    .map { (prefix, uri) ->
                        if (prefix.isEmpty()) "xmlns" to uri else "xmlns:$prefix" to uri
                    }
                    .toMap() + element.attributes
        )

    serializeXmlElement(elementWithXmlnsAttributes, sb)

    return sb.toString()
}

private fun serializeXmlElement(element: XmlNode.Element, out: StringBuilder) {
    out.append("<").append(element.tagName)

    for ((name, value) in element.attributes) {
        out.append(" ").append(name).append("=\"").append(escapeXml(value)).append("\"")
    }

    if (element.children.isEmpty()) {
        out.append("/>")
        return
    }

    out.append(">")

    for (child in element.children) {
        when (child) {
            is XmlNode.Text -> out.append(escapeXml(child.text))
            is XmlNode.Element -> serializeXmlElement(child, out)
        }
    }

    out.append("</").append(element.tagName).append(">")
}

private fun escapeXml(text: String): String {
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}
