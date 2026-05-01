package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class Code : StructuredValue(), NamedTypeValue {
    override val type = codeTypeName

    override val elements: MutableMap<kotlin.String, Value?>
        get() =
            mutableMapOf(
                "code" to code?.toCqlString(),
                "system" to system?.toCqlString(),
                "version" to version?.toCqlString(),
                "display" to display?.toCqlString(),
            )

    var code: kotlin.String? = null

    fun withCode(code: kotlin.String?): Code {
        this.code = code
        return this
    }

    var display: kotlin.String? = null

    fun withDisplay(display: kotlin.String?): Code {
        this.display = display
        return this
    }

    var system: kotlin.String? = null

    fun withSystem(system: kotlin.String?): Code {
        this.system = system
        return this
    }

    var version: kotlin.String? = null

    fun withVersion(version: kotlin.String?): Code {
        this.version = version
        return this
    }

    override fun toString(): kotlin.String {
        return "Code { code: ${this.code}, system: ${this.system}, version: ${this.version}, display: ${this.display} }"
    }
}
