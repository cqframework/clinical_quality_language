package org.opencds.cqf.cql.engine.data

import org.opencds.cqf.cql.engine.elm.executing.obfuscate.NoOpPHIObfuscator
import org.opencds.cqf.cql.engine.elm.executing.obfuscate.PHIObfuscator
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider

interface DataProvider : ModelResolver, RetrieveProvider {
    fun phiObfuscationSupplier(): () -> PHIObfuscator? {
        return { NoOpPHIObfuscator() }
    }
}
