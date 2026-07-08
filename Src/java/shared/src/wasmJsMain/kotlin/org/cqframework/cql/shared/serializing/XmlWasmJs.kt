@file:OptIn(ExperimentalWasmJsInterop::class)

/*
 * This file contains the implementation of XML parsing and serialization
 * for the WASM/JS target.
 */

package org.cqframework.cql.shared.serializing

import kotlin.js.ExperimentalWasmJsInterop

/**
 * Returns true if the DOM API is available in the current environment for XML parsing and
 * serialization.
 */
val domApiIsAvailable: Boolean =
    js(
        "typeof document !== 'undefined' && typeof DOMParser !== 'undefined' && typeof XMLSerializer !== 'undefined'"
    )

actual fun parseXml(xml: String): XmlNode.Element {
    return if (domApiIsAvailable) {
        parseXmlUsingDomApi(xml)
    } else {
        parseXmlUsingPolyfill(xml)
    }
}

actual fun toXmlString(element: XmlNode.Element, namespaces: Map<String, String>): String {
    return if (domApiIsAvailable) {
        serializeUsingDomApi(element, namespaces)
    } else {
        serializeUsingPolyfill(element, namespaces)
    }
}
