package org.opencds.cqf.cql.engine.execution

import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.Value

@JsOnlyExport
class ExpressionResult(
    val value: Value?,
    @Deprecated(
        "Automatic tracking of evaluated resources per expression is deprecated. To report the evidence behind a result, consider using supporting evidence expressions instead."
    )
    val evaluatedResources: MutableSet<Value?>?,
)
