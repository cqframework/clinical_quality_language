package org.opencds.cqf.cql.engine.execution

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class ExpressionResult(val value: Any?, val evaluatedResources: MutableSet<Any?>?)
