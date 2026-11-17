package org.opencds.cqf.cql.engine.runtime

import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import org.cqframework.cql.shared.JsOnlyExport

@JsOnlyExport
class ValueSet
@JvmOverloads
constructor(
    override val id: kotlin.String? = null,
    override val version: kotlin.String? = null,
    override val name: kotlin.String? = null,
    val codeSystems: kotlin.collections.List<CodeSystem>? = null,
) : Vocabulary {
    override val type = valueSetTypeName

    override val elements =
        mapOf(
            "id" to id?.toCqlString(),
            "version" to version?.toCqlString(),
            "name" to name?.toCqlString(),
            "codesystems" to codeSystems?.toCqlList(), // "codesystems" and not "codeSystems"
        )

    fun withId(id: kotlin.String?): ValueSet {
        return ValueSet(id, version, name, codeSystems)
    }

    fun withVersion(version: kotlin.String?): ValueSet {
        return ValueSet(id, version, name, codeSystems)
    }

    fun withName(name: kotlin.String?): ValueSet {
        return ValueSet(id, version, name, codeSystems)
    }

    fun withCodeSystems(codeSystems: kotlin.collections.List<CodeSystem>?): ValueSet {
        return ValueSet(id, version, name, codeSystems)
    }

    fun withCodeSystem(codeSystem: CodeSystem): ValueSet {
        return ValueSet(id, version, name, (codeSystems ?: listOf()) + codeSystem)
    }

    @JsName("getCodeSystemById")
    fun getCodeSystem(id: kotlin.String?): CodeSystem? {
        if (id == null) {
            return null
        }

        return codeSystems?.firstOrNull { it.id == id }
    }

    @JsName("getCodeSystemByIdAndVersion")
    fun getCodeSystem(id: kotlin.String?, version: kotlin.String?): CodeSystem? {
        if (id == null) {
            return null
        }

        return codeSystems?.firstOrNull {
            it.id == id &&
                ((version == null && it.version == null) ||
                    (version != null && it.version == version))
        }
    }

    private val hash: Int by lazy { listOf(id, version, name, codeSystems).hashCode() }

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): kotlin.Boolean =
        this === other ||
            (other is ValueSet &&
                id == other.id &&
                version == other.version &&
                name == other.name &&
                codeSystems == other.codeSystems)

    override fun toString(): kotlin.String = toStructuredValueString()
}
