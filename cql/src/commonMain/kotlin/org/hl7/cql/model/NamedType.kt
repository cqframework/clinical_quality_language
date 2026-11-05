package org.hl7.cql.model

import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
interface NamedType {
    val name: String
    val namespace: String
    val simpleName: String
    var target: String?
}
