package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
sealed interface Vocabulary : StructuredValue, NamedTypeValue {
    val id: kotlin.String?

    val version: kotlin.String?

    val name: kotlin.String?
}
