package org.hl7.cql.model

data class ModelIdentifier(
    var id: String,
    var system: String? = null,
    var version: String? = null,
) {
    init {
        require(id.isNotEmpty()) { "id can not be empty" }
    }
}
