package org.opencds.cqf.cql.engine.terminology

import org.opencds.cqf.cql.engine.runtime.ValueSet

class ValueSetInfo {
    var id: String? = null

    fun withId(id: String?): ValueSetInfo {
        this.id = id
        return this
    }

    var version: String? = null

    fun withVersion(version: String?): ValueSetInfo {
        this.version = version
        return this
    }

    var codeSystems: MutableList<CodeSystemInfo?>? = null
        get() {
            if (field == null) {
                field = ArrayList()
            }
            return field
        }
        private set

    fun withCodeSystem(codeSystem: CodeSystemInfo?): ValueSetInfo {
        this.codeSystems!!.add(codeSystem)
        return this
    }

    companion object {
        fun fromValueSet(vs: ValueSet): ValueSetInfo {
            val vsi = ValueSetInfo().withId(vs.id).withVersion(vs.version)
            for (cs in vs.codeSystems) {
                vsi.withCodeSystem(CodeSystemInfo.fromCodeSystem(cs))
            }
            return vsi
        }
    }
}
