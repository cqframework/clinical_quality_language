package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmOverloads
import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
data class Code
@JvmOverloads
constructor(
    val code: kotlin.String? = null,
    val display: kotlin.String? = null,
    val system: kotlin.String? = null,
    val version: kotlin.String? = null,
) : StructuredValue, NamedTypeValue {
    override val type = codeTypeName

    override val elements =
        mapOf(
            "code" to code?.toCqlString(),
            "system" to system?.toCqlString(),
            "version" to version?.toCqlString(),
            "display" to display?.toCqlString(),
        )

    fun withCode(code: kotlin.String?): Code {
        return Code(code, display, system, version)
    }

    fun withDisplay(display: kotlin.String?): Code {
        return Code(code, display, system, version)
    }

    fun withSystem(system: kotlin.String?): Code {
        return Code(code, display, system, version)
    }

    fun withVersion(version: kotlin.String?): Code {
        return Code(code, display, system, version)
    }

    private val hash: Int by lazy { listOf(code, display, system, version).hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other ||
            (other is Code &&
                code == other.code &&
                display == other.display &&
                system == other.system &&
                version == other.version)

    override fun toString(): kotlin.String = toStructuredValueString()
}
