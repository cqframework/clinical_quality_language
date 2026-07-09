@file:Suppress("PackageNaming", "MatchingDeclarationName")

package org.cqframework.cql.shared.serializing

import org.cqframework.cql.shared.QName

/** Used as an intermediate, cross-platform representation of an XML element or text node. */
sealed class XmlNode {
    data class Text(val text: String) : XmlNode()

    data class Element(
        val tagName: String,
        val attributes: Map<String, String>,
        val children: List<XmlNode>,
    ) : XmlNode()
}

/** A mutable version of [XmlNode] used during parsing. */
internal sealed class MutableXmlNode {
    data class Text(val text: String) : MutableXmlNode() {
        override fun toImmutable(): XmlNode.Text {
            return XmlNode.Text(text)
        }
    }

    data class Element(
        val tagName: String,
        val attributes: Map<String, String>,
        val children: MutableList<MutableXmlNode>,
    ) : MutableXmlNode() {
        override fun toImmutable(): XmlNode.Element {
            return XmlNode.Element(tagName, attributes, children.map { it.toImmutable() })
        }
    }

    abstract fun toImmutable(): XmlNode
}

/**
 * Parses the given XML string into a tree of `XmlNode` objects.
 *
 * @param xml The XML string to parse.
 * @return The root element of the parsed XML as an `XmlNode.Element`.
 */
expect fun parseXml(xml: String): XmlNode.Element

/**
 * Serializes the given `XmlNode.Element` to an XML document string.
 *
 * @param element The `XmlNode.Element` to convert.
 * @param namespaces A map of namespace prefixes to URIs.
 * @return The XML document string.
 */
expect fun toXmlString(element: XmlNode.Element, namespaces: Map<String, String>): String

/**
 * Parses the qualified name from an XML attribute value or tag name.
 *
 * @param value The qualified name string, which may include a prefix, e.g. "library",
 *   "fhir:Patient".
 * @param namespaces A map of namespace prefixes to URIs used in the current context.
 * @return The corresponding `QName` object.
 */
@Suppress("ThrowsCount")
fun xmlAttributeValueToQName(value: String, namespaces: Map<String, String>): QName {
    val parts = value.split(":")
    return when (parts.size) {
        1 ->
            QName(
                namespaces[""] ?: throw IllegalArgumentException("No default namespace found"),
                parts[0],
            )
        2 -> {
            val prefix = parts[0]
            QName(
                namespaces[prefix]
                    ?: throw IllegalArgumentException("No namespace found for prefix: $prefix"),
                parts[1],
                prefix,
            )
        }
        else -> throw IllegalArgumentException("Invalid QName format: $value")
    }
}

/**
 * Generates a new namespace prefix that isn't yet used in the current XML document.
 *
 * @param namespaces The map of existing namespace prefixes to URIs.
 * @return A new unique namespace prefix.
 */
private fun getNewKey(namespaces: Map<String, String>): String {
    val prefix = "ns"
    var i = 0
    while (namespaces.containsKey("$prefix$i")) {
        i++
    }
    return "$prefix$i"
}

/**
 * Converts a `QName` to a string for use in an XML attribute or tag name.
 *
 * @param qname The `QName` to convert.
 * @param namespaces A map of namespace prefixes to URIs assigned in the current XML document.
 * @param defaultNamespaces Prefixes to use for well-known namespaces, e.g. "a" for
 *   "urn:hl7-org:cql-annotations:r1".
 * @return The qualified name string, e.g. "a:CqlToElmInfo".
 */
fun qNameToXmlAttributeValue(
    qname: QName,
    namespaces: MutableMap<String, String>,
    defaultNamespaces: Map<String, String>,
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
