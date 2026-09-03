package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.Value

/** Represent the result of evaluating an expression. */
@JsOnlyExport
class ExpressionResult(
    /** The value of the expression. */
    val value: Value?,
    /** The evaluated resources, keyed by their string IDs. */
    val evaluatedResources: Map<kotlin.String, Value>,
)
