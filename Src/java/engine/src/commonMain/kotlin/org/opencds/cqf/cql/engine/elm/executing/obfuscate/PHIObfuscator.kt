package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
interface PHIObfuscator {
    fun obfuscate(source: Any?): String?
}
