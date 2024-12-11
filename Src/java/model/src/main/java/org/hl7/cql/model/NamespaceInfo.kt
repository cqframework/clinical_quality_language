package org.hl7.cql.model

import java.util.*

data class NamespaceInfo(val name: String, val uri: String) {
    init {
        require(name.isNotEmpty() and uri.isNotEmpty()) { "name and uri are required" }
    }

    override fun toString(): String {
        return String.format(Locale.US, "%s: %s", name, uri)
    }
}
