package org.opencds.cqf.cql.engine.runtime

class CodeSystem : Vocabulary() {
    fun withId(id: String?): CodeSystem {
        this.id = id
        return this
    }

    fun withVersion(version: String?): CodeSystem {
        this.version = version
        return this
    }

    fun withName(name: String?): CodeSystem {
        this.name = name
        return this
    }

    override fun equivalent(other: Any?): Boolean? {
        if (other !is CodeSystem) {
            return false
        }
        return super.equivalent(other)
    }

    override fun equal(other: Any?): Boolean? {
        if (other !is CodeSystem) {
            return false
        }
        return super.equal(other)
    }
}
