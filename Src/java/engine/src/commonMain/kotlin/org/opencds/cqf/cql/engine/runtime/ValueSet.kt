package org.opencds.cqf.cql.engine.runtime

class ValueSet : Vocabulary() {
    fun withId(id: String?): ValueSet {
        this.id = id
        return this
    }

    fun withVersion(version: String?): ValueSet {
        this.version = version
        return this
    }

    fun withName(name: String?): ValueSet {
        this.name = name
        return this
    }

    var codeSystems = mutableListOf<CodeSystem>()
        private set

    fun setCodeSystems(codeSystems: MutableList<CodeSystem?>?) {
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

    fun getCodeSystem(id: String?): CodeSystem? {
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

    fun getCodeSystem(id: String?, version: String?): CodeSystem? {
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
