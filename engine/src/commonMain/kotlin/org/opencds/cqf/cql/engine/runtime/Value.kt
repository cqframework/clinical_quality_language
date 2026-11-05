package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.JsOnlyExport

/** Represents a non-null CQL value. */
@JsOnlyExport
sealed interface Value {
    val typeAsString: kotlin.String

    /** Returns the CVL representation of the CQL value. */
    abstract override fun toString(): kotlin.String
}
