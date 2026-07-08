package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import org.opencds.cqf.cql.engine.runtime.Value

class NoOpPHIObfuscator : PHIObfuscator {
    override fun obfuscate(source: Value?): kotlin.String {
        return source.toString()
    }
}
