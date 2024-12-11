package org.hl7.cql.model

class SearchType(val name: String, val path: String, val type: DataType) {

    init {
        require(name.isNotEmpty() && path.isNotEmpty()) { "name and path are required" }
    }
}
