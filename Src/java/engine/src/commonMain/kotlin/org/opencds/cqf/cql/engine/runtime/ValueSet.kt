package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsName
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class ValueSet : Vocabulary() {
    override val type = valueSetTypeName

    override val elements: MutableMap<kotlin.String, Value?>
        get() =
            mutableMapOf(
                "id" to id?.toCqlString(),
                "version" to version?.toCqlString(),
                "name" to name?.toCqlString(),
                "codesystems" to codeSystems.toCqlList(),
            )

    fun withId(id: kotlin.String?): ValueSet {
        this.id = id
        return this
    }

    fun withVersion(version: kotlin.String?): ValueSet {
        this.version = version
        return this
    }

    fun withName(name: kotlin.String?): ValueSet {
        this.name = name
        return this
    }

    var codeSystems = mutableListOf<CodeSystem>()
        private set

    @Suppress("NON_EXPORTABLE_TYPE")
    fun setCodeSystems(codeSystems: Iterable<CodeSystem?>?) {
        this.codeSystems = mutableListOf()
        if (codeSystems != null) {
            for (cs in codeSystems) {
                if (cs != null) {
                    addCodeSystem(cs)
                }
            }
        }
    }

    fun withCodeSystems(codeSystems: MutableList<CodeSystem?>?): ValueSet {
        setCodeSystems(codeSystems)
        return this
    }

    fun addCodeSystem(codeSystem: CodeSystem) {
        codeSystems.add(codeSystem)
    }

    fun withCodeSystem(codeSystem: CodeSystem): ValueSet {
        addCodeSystem(codeSystem)
        return this
    }

    @JsName("getCodeSystemById")
    fun getCodeSystem(id: kotlin.String?): CodeSystem? {
        if (id == null) {
            return null
        }

        for (cs in codeSystems) {
            if (id == cs.id) {
                return cs
            }
        }

        return null
    }

    @JsName("getCodeSystemByIdAndVersion")
    fun getCodeSystem(id: kotlin.String?, version: kotlin.String?): CodeSystem? {
        if (id == null) {
            return null
        }

        for (cs in codeSystems) {
            if (
                id == cs.id &&
                    ((version == null && cs.version == null) ||
                        (version != null && version == cs.version))
            ) {
                return cs
            }
        }

        return null
    }
}
