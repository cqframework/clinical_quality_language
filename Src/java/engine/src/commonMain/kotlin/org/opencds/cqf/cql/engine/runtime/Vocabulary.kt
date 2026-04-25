package org.opencds.cqf.cql.engine.runtime

sealed class Vocabulary : StructuredValue(), NamedCqlType {
    var id: kotlin.String? = null

    var version: kotlin.String? = null

    var name: kotlin.String? = null

    override fun toString(): kotlin.String {
        return toPrettyString(type.getLocalPart())
    }
}
