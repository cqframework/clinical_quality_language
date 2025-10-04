package org.opencds.cqf.cql.engine.elm.executing.obfuscate

class NoOpPHIObfuscator : PHIObfuscator {
    override fun obfuscate(source: Any?): String? {
        return source.toString()
    }
}
