package org.opencds.cqf.cql.engine.runtime

abstract class Vocabulary : CqlType {
    var id: String? = null

    var version: String? = null

    var name: String? = null
}
