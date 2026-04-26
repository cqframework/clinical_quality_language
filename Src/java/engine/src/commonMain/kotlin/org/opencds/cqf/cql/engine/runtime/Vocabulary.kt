package org.opencds.cqf.cql.engine.runtime

sealed class Vocabulary : StructuredValue(), NamedCqlType {
    var id: String? = null

    var version: String? = null

    var name: String? = null

    override fun toString(): String {
        return toPrettyString(type.getLocalPart())
    }
}
