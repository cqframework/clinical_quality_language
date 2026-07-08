package org.cqframework.cql.shared.serializing

import kotlin.js.collections.JsMap
import kotlin.js.collections.toMap

@JsModule("saxes")
private external object Saxes {
    class SaxesParser
}

@OptIn(ExperimentalJsCollectionsApi::class)
@Suppress("TooGenericExceptionThrown", "ThrowsCount")
internal fun parseXmlUsingPolyfill(xml: String): XmlNode.Element {
    val parser = Saxes.SaxesParser().asDynamic()

    val stack = mutableListOf(MutableXmlNode.Element("", emptyMap(), mutableListOf()))

    parser.on("text") { t -> stack.last().children.add(MutableXmlNode.Text(t as String)) }

    parser.on("opentag") { node ->
        val tagName = node.name as String
        @Suppress("UnusedPrivateProperty")
        val attributesEntries = js("Object").entries(node.attributes)
        val attributes = (js("new Map(attributesEntries)") as JsMap<dynamic, dynamic>).toMap()

        val newElement = MutableXmlNode.Element(tagName, attributes, mutableListOf())

        stack.last().children.add(newElement)
        stack.add(newElement)
    }

    parser.on("closetag") {
        if (stack.size < 2) {
            throw Exception("Parsing error: Unmatched closing tag")
        }
        stack.removeAt(stack.size - 1)
    }

    parser.on("error") { e -> throw Exception("Parsing error: ${e.message}") }

    parser.write(xml).close()

    if (stack.size != 1) {
        throw Exception("Parsing error: Unmatched opening tag(s)")
    }

    for (child in stack[0].children) {
        if (child is MutableXmlNode.Element) {
            return child.toImmutable()
        }
    }

    throw Exception("Parsing error: No root element found")
}
