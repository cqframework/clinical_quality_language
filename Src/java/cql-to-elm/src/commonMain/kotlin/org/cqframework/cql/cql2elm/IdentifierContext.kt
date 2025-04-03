package org.cqframework.cql.cql2elm

import kotlin.reflect.KClass
import org.hl7.elm.r1.Element

/**
 * Simple POJO using for identifier hider that maintains the identifier and Trackable type of the
 * construct being evaluated.
 */
class IdentifierContext(val identifier: String, val trackableSubclass: KClass<out Element>?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || this::class != other::class) {
            return false
        }
        val that = other as IdentifierContext
        return identifier == that.identifier && trackableSubclass == that.trackableSubclass
    }

    override fun hashCode(): Int {
        return identifier.hashCode() * 31 + trackableSubclass.hashCode()
    }

    override fun toString(): String {
        return listOf("identifier='$identifier'", "elementSubclass=$trackableSubclass")
            .joinToString(", ", IdentifierContext::class.simpleName + "[", "]")
    }
}
