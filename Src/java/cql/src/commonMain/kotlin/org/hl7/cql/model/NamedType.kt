package org.hl7.cql.model

interface NamedType {
    val name: String
    val namespace: String
    val simpleName: String
    var target: String?
}
