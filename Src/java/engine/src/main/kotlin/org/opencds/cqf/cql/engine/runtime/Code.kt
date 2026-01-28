package org.opencds.cqf.cql.engine.runtime

class Code : CqlType {
    var code: String? = null

    fun withCode(code: String?): Code {
        this.code = code
        return this
    }

    var display: String? = null

    fun withDisplay(display: String?): Code {
        this.display = display
        return this
    }

    var system: String? = null

    fun withSystem(system: String?): Code {
        this.system = system
        return this
    }

    var version: String? = null

    fun withVersion(version: String?): Code {
        this.version = version
        return this
    }

    override fun toString(): String {
        return "Code { code: ${this.code}, system: ${this.system}, version: ${this.version}, display: ${this.display} }"
    }
}
