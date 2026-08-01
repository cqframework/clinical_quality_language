package org.opencds.cqf.cql.engine.terminology

import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.CodeSystem

@JsOnlyExport
class CodeSystemInfo {
    var id: String? = null

    fun withId(id: String?): CodeSystemInfo {
        this.id = id
        return this
    }

    var version: String? = null

    fun withVersion(version: String?): CodeSystemInfo {
        this.version = version
        return this
    }

    companion object {
        fun fromCodeSystem(cs: CodeSystem): CodeSystemInfo {
            return CodeSystemInfo().withId(cs.id).withVersion(cs.version)
        }
    }
}
