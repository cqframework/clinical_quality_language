package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent

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

    override fun equivalent(other: Any?): Boolean? {
        return equivalent(this.code, (other as Code).code) == true &&
            equivalent(this.system, other.system) == true
    }

    override fun equal(other: Any?): Boolean? {
        var codeIsEqual = equal(this.code, (other as Code).code)
        var systemIsEqual = equal(this.system, other.system)
        var versionIsEqual = equal(this.version, other.version)
        var displayIsEqual = equal(this.display, other.display)
        if (codeIsEqual == null && this.code == null && other.code == null) {
            codeIsEqual = true
        }
        if (systemIsEqual == null && this.system == null && other.system == null) {
            systemIsEqual = true
        }
        if (versionIsEqual == null && this.version == null && other.version == null) {
            versionIsEqual = true
        }
        if (displayIsEqual == null && this.display == null && other.display == null) {
            displayIsEqual = true
        }
        return if (
            codeIsEqual == null ||
                systemIsEqual == null ||
                versionIsEqual == null ||
                displayIsEqual == null
        )
            null
        else codeIsEqual && systemIsEqual && versionIsEqual && displayIsEqual
    }

    override fun toString(): String {
        return String.format(
            "Code { code: %s, system: %s, version: %s, display: %s }",
            this.code,
            this.system,
            this.version,
            this.display,
        )
    }
}
