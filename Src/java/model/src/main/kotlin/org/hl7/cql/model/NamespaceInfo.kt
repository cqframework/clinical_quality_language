package org.hl7.cql.model

data class NamespaceInfo(val name: String, val uri: String) {
    init {
        require(name.isNotEmpty() and uri.isNotEmpty()) { "name and uri can not be empty" }
    }

    override fun toString(): String {
        return "$name: $uri"
    }
}
