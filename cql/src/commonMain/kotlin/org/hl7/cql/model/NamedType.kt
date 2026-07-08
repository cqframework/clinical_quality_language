package org.hl7.cql.model

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
interface NamedType {
    val name: String
    val namespace: String
    val simpleName: String
    var target: String?
}
