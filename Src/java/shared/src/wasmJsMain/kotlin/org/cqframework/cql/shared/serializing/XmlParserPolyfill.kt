@file:OptIn(ExperimentalWasmJsInterop::class)

package org.cqframework.cql.shared.serializing

import kotlin.js.ExperimentalWasmJsInterop

@JsModule("saxes")
private external object Saxes {
    class SaxesParser() {
        fun on(event: String, callback: (data: JsAny) -> Unit)

        fun write(data: String): SaxesParser

        fun close(): SaxesParser
    }
}

private external interface SaxesNode : JsAny {
    val name: String
    val attributes: JsAny
}

@JsName("Object")
private external object Object {
    fun entries(obj: JsAny): JsArray<JsArray<JsAny>>
}

@JsName("Error")
private external class Error : JsAny {
    val message: String
}

internal fun parseXmlUsingPolyfill(xml: String): XmlNode.Element {
    val parser = Saxes.SaxesParser()

    val stack = mutableListOf(MutableXmlNode.Element("", emptyMap(), mutableListOf()))

    parser.on("text") { data ->
        val t = data.unsafeCast<JsString>().toString()
        stack.last().children.add(MutableXmlNode.Text(t))
    }

    parser.on("opentag") { data ->
        val node = data.unsafeCast<SaxesNode>()
        val tagName = node.name
        val attributesEntries = Object.entries(node.attributes).toList()
        val attributes =
            attributesEntries.associate {
                it[0]!!.unsafeCast<JsString>().toString() to
                    it[1]!!.unsafeCast<JsString>().toString()
            }

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

    parser.on("error") { data ->
        val e = data.unsafeCast<Error>()
        throw Exception("Parsing error: ${e.message}")
    }

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
