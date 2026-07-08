package org.opencds.cqf.cql.engine.execution

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.Value

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class ExpressionResult(val value: Value?, val evaluatedResources: MutableSet<Value?>?)
