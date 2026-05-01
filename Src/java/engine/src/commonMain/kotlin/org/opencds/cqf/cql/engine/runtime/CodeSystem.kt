package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class CodeSystem : Vocabulary() {
    override val type = codeSystemTypeName

    override val elements: MutableMap<kotlin.String, Value?>
        get() =
            mutableMapOf(
                "id" to id?.toCqlString(),
                "version" to version?.toCqlString(),
                "name" to name?.toCqlString(),
            )

    fun withId(id: kotlin.String?): CodeSystem {
        this.id = id
        return this
    }

    fun withVersion(version: kotlin.String?): CodeSystem {
        this.version = version
        return this
    }

    fun withName(name: kotlin.String?): CodeSystem {
        this.name = name
        return this
    }
}
