package org.cqframework.cql.cql2elm

import java.util.*
import org.cqframework.cql.elm.tracking.Trackable

/**
 * Simple POJO using for identifier hider that maintains the identifier and Trackable type of the
 * construct being evaluated.
 */
class IdentifierContext(val identifier: String, val trackableSubclass: Class<out Trackable>?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as IdentifierContext
        return identifier == that.identifier && trackableSubclass == that.trackableSubclass
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier, trackableSubclass)
    }

    override fun toString(): String {
        return StringJoiner(", ", IdentifierContext::class.java.simpleName + "[", "]")
            .add("identifier='$identifier'")
            .add("elementSubclass=$trackableSubclass")
            .toString()
    }
}
