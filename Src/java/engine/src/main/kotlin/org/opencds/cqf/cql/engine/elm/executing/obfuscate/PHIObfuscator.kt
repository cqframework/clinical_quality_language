package org.opencds.cqf.cql.engine.elm.executing.obfuscate

interface PHIObfuscator {
    fun obfuscate(source: Any?): String?
}
