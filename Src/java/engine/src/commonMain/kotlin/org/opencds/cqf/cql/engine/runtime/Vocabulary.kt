package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
abstract class Vocabulary : CqlType {
    var id: String? = null

    var version: String? = null

    var name: String? = null
}
