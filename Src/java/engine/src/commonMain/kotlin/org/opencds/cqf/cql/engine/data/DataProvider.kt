package org.opencds.cqf.cql.engine.data

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.NoOpPHIObfuscator
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.PHIObfuscator
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
interface DataProvider : ModelResolver, RetrieveProvider {
    fun phiObfuscationSupplier(): () -> PHIObfuscator? {
        return { NoOpPHIObfuscator() }
    }
}
