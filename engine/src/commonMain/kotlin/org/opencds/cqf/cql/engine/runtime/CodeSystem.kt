package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmOverloads
import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
class CodeSystem
@JvmOverloads
constructor(
    override val id: kotlin.String? = null,
    override val version: kotlin.String? = null,
    override val name: kotlin.String? = null,
) : Vocabulary {
    override val type = codeSystemTypeName

    override val elements =
        mapOf(
            "id" to id?.toCqlString(),
            "version" to version?.toCqlString(),
            "name" to name?.toCqlString(),
        )

    fun withId(id: kotlin.String?): CodeSystem {
        return CodeSystem(id, version, name)
    }

    fun withVersion(version: kotlin.String?): CodeSystem {
        return CodeSystem(id, version, name)
    }

    fun withName(name: kotlin.String?): CodeSystem {
        return CodeSystem(id, version, name)
    }

    private val hash: Int by lazy { listOf(id, version, name).hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other ||
            (other is CodeSystem &&
                id == other.id &&
                version == other.version &&
                name == other.name)

    override fun toString(): kotlin.String = toStructuredValueString()
}
