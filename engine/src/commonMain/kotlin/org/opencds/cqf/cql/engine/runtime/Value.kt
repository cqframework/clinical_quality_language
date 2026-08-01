package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.elm.executing.PropertyEvaluator

/** Represents a non-null CQL value. */
@JsOnlyExport
sealed interface Value {
    val typeAsString: kotlin.String

    fun get(path: kotlin.String): Value? {
        return PropertyEvaluator.resolvePath(this, path)
    }

    /** Returns the CVL representation of the CQL value. */
    abstract override fun toString(): kotlin.String
}
