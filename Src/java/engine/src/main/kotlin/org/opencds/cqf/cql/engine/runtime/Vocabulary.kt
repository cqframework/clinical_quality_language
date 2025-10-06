package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.elm.executing.AndEvaluator.and
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.elm.executing.OrEvaluator.or

abstract class Vocabulary : CqlType {
    var id: String? = null

    var version: String? = null

    var name: String? = null

    override fun equivalent(other: Any?): Boolean? {
        if (other !is Vocabulary) {
            return false
        }
        return equivalent(version, other.version)
    }

    override fun equal(other: Any?): Boolean? {
        if (other !is Vocabulary) {
            return false
        }
        return and(
            or(id == null && other.id == null, equal(id, other.id)),
            or(version == null && other.version == null, equal(version, other.version)),
        )
    }
}
