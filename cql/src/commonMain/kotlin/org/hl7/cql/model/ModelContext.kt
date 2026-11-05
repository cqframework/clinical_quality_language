package org.hl7.cql.model

/** Created by Bryn on 3/20/2019. */
class ModelContext(
    val name: String,
    val type: ClassType,
    val keys: List<String>,
    val birthDateElement: String? = null,
)
