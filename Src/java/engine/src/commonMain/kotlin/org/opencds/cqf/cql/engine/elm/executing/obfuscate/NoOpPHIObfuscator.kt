package org.opencds.cqf.cql.engine.elm.executing.obfuscate

import org.opencds.cqf.cql.engine.runtime.CqlType

class NoOpPHIObfuscator : PHIObfuscator {
    override fun obfuscate(source: CqlType?): kotlin.String {
        return source.toString()
    }
}
