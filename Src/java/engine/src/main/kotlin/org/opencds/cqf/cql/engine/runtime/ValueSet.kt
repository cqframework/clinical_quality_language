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

    private var codeSystems = mutableListOf<CodeSystem>()

    fun getCodeSystems(): Iterable<CodeSystem> {
        return codeSystems
    }

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

    override fun equivalent(other: Any?): Boolean? {
        if (other !is ValueSet) {
            return false
        }
        val otherV = other
        val equivalent =
            super.equivalent(other) == true && codeSystems.size == otherV.codeSystems.size
        if (equivalent) {
            for (cs in codeSystems) {
                val otherC = otherV.getCodeSystem(cs.id)
                if (otherC == null) {
                    return false
                }
            }
        }
        return equivalent
    }

    override fun equal(other: Any?): Boolean? {
        if (other !is ValueSet) {
            return false
        }
        val equal = super.equal(other) == true && codeSystems.size == other.codeSystems.size
        if (equal) {
            for (cs in codeSystems) {
                val otherC = other.getCodeSystem(cs.id, cs.version)
                if (otherC == null) {
                    return false
                }
            }
        }
        return equal
    }
}
