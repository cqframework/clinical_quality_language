package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
sealed class Vocabulary : StructuredValue(), NamedTypeValue {
    var id: kotlin.String? = null

    var version: kotlin.String? = null

    var name: kotlin.String? = null

    override fun toString(): kotlin.String {
        return toPrettyString(type.getLocalPart())
    }
}
