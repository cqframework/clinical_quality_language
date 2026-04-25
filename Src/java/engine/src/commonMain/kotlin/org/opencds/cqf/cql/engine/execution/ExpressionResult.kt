package org.opencds.cqf.cql.engine.execution

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.opencds.cqf.cql.engine.runtime.CqlType

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
class ExpressionResult(val value: CqlType?, val evaluatedResources: MutableSet<CqlType?>?)
