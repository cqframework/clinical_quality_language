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
}
