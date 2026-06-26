package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

/** Represents a non-null CQL value. */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
sealed interface Value {
    val typeAsString: kotlin.String

    /** Returns the CVL representation of the CQL value. */
    abstract override fun toString(): kotlin.String
}
